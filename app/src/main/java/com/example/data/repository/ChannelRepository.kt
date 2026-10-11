package com.example.data.repository

import android.content.Context
import com.example.data.model.Channel
import com.example.data.model.Playlist
import com.example.data.parser.M3uParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

class ChannelRepository(context: Context) {

  companion object {
    const val DEFAULT_PLAYLIST_URL = "https://melbourne-tv.np6186.workers.dev/au/Melbourne/raw-tv.m3u8"
    const val DEFAULT_PLAYLIST_NAME = "Melbourne Free-to-Air"
    private const val PREFS_NAME = "comfort_inn_tv_prefs"
    private const val KEY_PLAYLISTS = "custom_playlists"
    private const val KEY_ACTIVE_PLAYLIST_ID = "active_playlist_id"
    private const val KEY_FAVORITES = "favorite_channel_ids"
  }

  private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  private val httpClient = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .followRedirects(true)
    .build()

  // In-memory cache
  private var cachedChannels: List<Channel> = emptyList()

  fun getPlaylists(): List<Playlist> {
    val defaultPlaylist = Playlist(
      id = "default_melbourne",
      name = DEFAULT_PLAYLIST_NAME,
      url = DEFAULT_PLAYLIST_URL,
      isDefault = true,
      channelCount = cachedChannels.size
    )

    val savedJson = prefs.getString(KEY_PLAYLISTS, null) ?: return listOf(defaultPlaylist)
    return try {
      val jsonArray = JSONArray(savedJson)
      val list = mutableListOf(defaultPlaylist)
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          Playlist(
            id = obj.getString("id"),
            name = obj.getString("name"),
            url = obj.getString("url"),
            isDefault = false,
            channelCount = obj.optInt("channelCount", 0)
          )
        )
      }
      list
    } catch (e: Exception) {
      listOf(defaultPlaylist)
    }
  }

  fun getActivePlaylist(): Playlist {
    val activeId = prefs.getString(KEY_ACTIVE_PLAYLIST_ID, "default_melbourne") ?: "default_melbourne"
    val all = getPlaylists()
    return all.find { it.id == activeId } ?: all.first()
  }

  fun setActivePlaylist(playlistId: String) {
    prefs.edit().putString(KEY_ACTIVE_PLAYLIST_ID, playlistId).apply()
  }

  fun addPlaylist(name: String, url: String): Playlist {
    val newPlaylist = Playlist(
      id = UUID.randomUUID().toString(),
      name = name.ifBlank { "Custom Playlist" },
      url = url.trim(),
      isDefault = false,
      channelCount = 0
    )

    val currentList = getPlaylists().filter { !it.isDefault }.toMutableList()
    currentList.add(newPlaylist)

    val jsonArray = JSONArray()
    for (p in currentList) {
      val obj = JSONObject().apply {
        put("id", p.id)
        put("name", p.name)
        put("url", p.url)
        put("channelCount", p.channelCount)
      }
      jsonArray.put(obj)
    }
    prefs.edit().putString(KEY_PLAYLISTS, jsonArray.toString()).apply()
    return newPlaylist
  }

  fun deletePlaylist(playlistId: String) {
    val currentList = getPlaylists().filter { !it.isDefault && it.id != playlistId }
    val jsonArray = JSONArray()
    for (p in currentList) {
      val obj = JSONObject().apply {
        put("id", p.id)
        put("name", p.name)
        put("url", p.url)
        put("channelCount", p.channelCount)
      }
      jsonArray.put(obj)
    }
    prefs.edit()
      .putString(KEY_PLAYLISTS, jsonArray.toString())
      .apply()

    if (prefs.getString(KEY_ACTIVE_PLAYLIST_ID, "") == playlistId) {
      setActivePlaylist("default_melbourne")
    }
  }

  fun getFavorites(): Set<String> {
    return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
  }

  fun toggleFavorite(channelId: String): Boolean {
    val current = getFavorites().toMutableSet()
    val isFav: Boolean
    if (current.contains(channelId)) {
      current.remove(channelId)
      isFav = false
    } else {
      current.add(channelId)
      isFav = true
    }
    prefs.edit().putStringSet(KEY_FAVORITES, current).apply()

    // Update in-memory cached channels
    cachedChannels = cachedChannels.map {
      if (it.id == channelId) it.copy(isFavorite = isFav) else it
    }
    return isFav
  }

  suspend fun fetchChannels(playlistUrl: String = getActivePlaylist().url): Result<List<Channel>> = withContext(Dispatchers.IO) {
    try {
      val request = Request.Builder()
        .url(playlistUrl)
        .header("User-Agent", "otg/1.5.1 (AppleTv Apple TV 4; tvOS16.0; appletv.client)")
        .build()

      httpClient.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
          if (cachedChannels.isNotEmpty()) {
            return@withContext Result.success(cachedChannels)
          }
          return@withContext Result.success(getFallbackChannels())
        }

        val content = response.body?.string() ?: ""
        if (content.isBlank()) {
          return@withContext Result.success(getFallbackChannels())
        }

        val parsed = M3uParser.parse(content, playlistUrl)
        val favorites = getFavorites()
        val channelsWithFavs = parsed.map { channel ->
          channel.copy(isFavorite = favorites.contains(channel.id))
        }

        cachedChannels = if (channelsWithFavs.isNotEmpty()) channelsWithFavs else getFallbackChannels()
        Result.success(cachedChannels)
      }
    } catch (e: Exception) {
      if (cachedChannels.isNotEmpty()) {
        Result.success(cachedChannels)
      } else {
        Result.success(getFallbackChannels())
      }
    }
  }

  fun getCachedChannels(): List<Channel> {
    if (cachedChannels.isEmpty()) {
      cachedChannels = getFallbackChannels()
    }
    return cachedChannels
  }

  private fun getFallbackChannels(): List<Channel> {
    val favorites = getFavorites()
    return listOf(
      Channel(
        id = "mjh-seven-mel",
        name = "Seven",
        streamUrl = "https://i.mjh.nz/.r/seven-mel.m3u8",
        logoUrl = "https://i.mjh.nz/.images/auto-seven-mel-9608a4.png",
        channelNumber = "71",
        group = "Melbourne",
        userAgent = "otg/1.5.1 (AppleTv Apple TV 4; tvOS16.0; appletv.client)",
        referrer = "https://i.mjh.nz/.r/seven-mel.m3u8",
        isFavorite = favorites.contains("mjh-seven-mel")
      ),
      Channel(
        id = "mjh-7two-mel",
        name = "7two",
        streamUrl = "https://i.mjh.nz/.r/7two-mel.m3u8",
        logoUrl = "https://i.mjh.nz/.images/auto-7two-mel-701123.png",
        channelNumber = "72",
        group = "Melbourne",
        userAgent = "otg/1.5.1 (AppleTv Apple TV 4; tvOS16.0; appletv.client)",
        referrer = "https://i.mjh.nz/.r/7two-mel.m3u8",
        isFavorite = favorites.contains("mjh-7two-mel")
      ),
      Channel(
        id = "mjh-7mate-mel",
        name = "7mate",
        streamUrl = "https://i.mjh.nz/.r/7mate-mel.m3u8",
        logoUrl = "https://i.mjh.nz/.images/auto-7mate-mel-360d60.png",
        channelNumber = "74",
        group = "Melbourne",
        userAgent = "otg/1.5.1 (AppleTv Apple TV 4; tvOS16.0; appletv.client)",
        referrer = "https://i.mjh.nz/.r/7mate-mel.m3u8",
        isFavorite = favorites.contains("mjh-7mate-mel")
      ),
      Channel(
        id = "mjh-channel-9-vic",
        name = "Channel 9",
        streamUrl = "https://i.mjh.nz/.r/channel-9-vic.m3u8",
        logoUrl = "https://imageresizer.static9.net.au/lx1XIWcrYZRtnfJOqvxNn9CshDI=/768x0/https%3A%2F%2Fvms-tv-images-prod.s3-ap-southeast-2.amazonaws.com%2F2024%2F09%2F630608%2F9nine-1.png",
        channelNumber = "91",
        group = "Melbourne",
        userAgent = "otg/1.5.1 (AppleTv Apple TV 4; tvOS16.0; appletv.client)",
        referrer = "https://i.mjh.nz/.r/channel-9-vic.m3u8",
        isFavorite = favorites.contains("mjh-channel-9-vic")
      ),
      Channel(
        id = "mjh-gem-vic",
        name = "9Gem",
        streamUrl = "https://i.mjh.nz/.r/gem-vic.m3u8",
        logoUrl = "https://imageresizer.static9.net.au/d00389KKc5T4o5isRaDE9Pl9DWk=/768x0/https%3A%2F%2Fvms-tv-images-prod.s3-ap-southeast-2.amazonaws.com%2F2024%2F08%2F620929%2F9gem-1.png",
        channelNumber = "92",
        group = "Melbourne",
        userAgent = "otg/1.5.1 (AppleTv Apple TV 4; tvOS16.0; appletv.client)",
        referrer = "https://i.mjh.nz/.r/gem-vic.m3u8",
        isFavorite = favorites.contains("mjh-gem-vic")
      ),
      Channel(
        id = "mjh-go-vic",
        name = "9Go!",
        streamUrl = "https://i.mjh.nz/.r/go-vic.m3u8",
        logoUrl = "https://imageresizer.static9.net.au/fcTwzkAJT4q81emfp2kYkgoxlDA=/768x0/https%3A%2F%2Fvms-tv-images-prod.s3-ap-southeast-2.amazonaws.com%2F2024%2F03%2F574470%2F9go.png",
        channelNumber = "93",
        group = "Melbourne",
        userAgent = "otg/1.5.1 (AppleTv Apple TV 4; tvOS16.0; appletv.client)",
        referrer = "https://i.mjh.nz/.r/go-vic.m3u8",
        isFavorite = favorites.contains("mjh-go-vic")
      ),
      Channel(
        id = "mjh-abc-vic",
        name = "ABC TV",
        streamUrl = "https://i.mjh.nz/.r/abc-vic.m3u8",
        logoUrl = "https://i.mjh.nz/.images/abc-nsw.png",
        channelNumber = "21",
        group = "Melbourne",
        userAgent = "otg/1.5.1 (AppleTv Apple TV 4; tvOS16.0; appletv.client)",
        referrer = "https://i.mjh.nz/.r/abc-vic.m3u8",
        isFavorite = favorites.contains("mjh-abc-vic")
      ),
      Channel(
        id = "mjh-10-vic",
        name = "10 Melbourne",
        streamUrl = "https://i.mjh.nz/.r/10-vic.m3u8",
        logoUrl = "https://10play.com.au/ip/s3/2025/06/25/b18ae76555d114f7bfaf1c3bfa43dd3f-1384577.png",
        channelNumber = "10",
        group = "Melbourne",
        userAgent = "otg/1.5.1 (AppleTv Apple TV 4; tvOS16.0; appletv.client)",
        referrer = "https://i.mjh.nz/.r/10-vic.m3u8",
        isFavorite = favorites.contains("mjh-10-vic")
      )
    )
  }
}
