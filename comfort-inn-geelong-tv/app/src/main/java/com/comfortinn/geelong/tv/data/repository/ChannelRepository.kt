package com.comfortinn.geelong.tv.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.comfortinn.geelong.tv.data.local.AppDatabase
import com.comfortinn.geelong.tv.data.local.ChannelEntity
import com.comfortinn.geelong.tv.data.local.ProgramEntity
import com.comfortinn.geelong.tv.data.parser.M3u8Parser
import com.comfortinn.geelong.tv.data.parser.XmltvParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.util.concurrent.TimeUnit
import java.util.zip.GZIPInputStream

class ChannelRepository(private val context: Context) {
    private val TAG = "ChannelRepository"
    private val db = AppDatabase.getInstance(context)
    private val channelDao = db.channelDao()
    private val programDao = db.programDao()

    private val prefs: SharedPreferences = context.getSharedPreferences("iptv_settings", Context.MODE_PRIVATE)

    companion object {
        const val DEFAULT_M3U_URL = "https://i.mjh.nz/au/Melbourne/kodi-tv.m3u8"
        const val DEFAULT_EPG_URL = "https://i.mjh.nz/au/Melbourne/epg.xml.gz"
        const val DEFAULT_EPG_FALLBACK_URL = "https://i.mjh.nz/au/Melbourne/epg.xml"
        private const val KEY_CUSTOM_M3U = "custom_m3u_url"
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val channelsFlow: Flow<List<ChannelEntity>> = channelDao.getAllChannelsFlow()

    fun getM3uUrl(): String {
        return prefs.getString(KEY_CUSTOM_M3U, DEFAULT_M3U_URL) ?: DEFAULT_M3U_URL
    }

    fun setM3uUrl(url: String) {
        val cleanUrl = url.trim()
        if (cleanUrl.isNotEmpty()) {
            prefs.edit().putString(KEY_CUSTOM_M3U, cleanUrl).apply()
        }
    }

    fun resetToDefaultM3uUrl() {
        prefs.edit().putString(KEY_CUSTOM_M3U, DEFAULT_M3U_URL).apply()
    }

    suspend fun getChannels(): List<ChannelEntity> = channelDao.getAllChannels()

    suspend fun getCurrentProgram(tvgId: String): ProgramEntity? {
        val now = System.currentTimeMillis()
        return programDao.getCurrentProgram(tvgId, now)
    }

    suspend fun getNextProgram(tvgId: String): ProgramEntity? {
        val now = System.currentTimeMillis()
        return programDao.getNextProgram(tvgId, now)
    }

    suspend fun refreshChannelsAndEpg(): Boolean = withContext(Dispatchers.IO) {
        try {
            val activeM3uUrl = getM3uUrl()
            Log.i(TAG, "Starting M3U & XMLTV sync from: $activeM3uUrl")

            val m3u8Request = Request.Builder().url(activeM3uUrl).build()
            val m3u8Response = httpClient.newCall(m3u8Request).execute()

            if (m3u8Response.isSuccessful && m3u8Response.body != null) {
                val channels = M3u8Parser.parse(m3u8Response.body!!.byteStream())
                if (channels.isNotEmpty()) {
                    channelDao.clearAll()
                    channelDao.insertChannels(channels)
                    Log.i(TAG, "Successfully parsed & stored ${channels.size} channels")
                }
            }

            var epgStream: InputStream? = null
            var isGz = false

            try {
                val gzReq = Request.Builder().url(DEFAULT_EPG_URL).build()
                val gzResp = httpClient.newCall(gzReq).execute()
                if (gzResp.isSuccessful && gzResp.body != null) {
                    epgStream = gzResp.body!!.byteStream()
                    isGz = true
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gzip EPG failed, trying uncompressed XML", e)
            }

            if (epgStream == null) {
                val xmlReq = Request.Builder().url(DEFAULT_EPG_FALLBACK_URL).build()
                val xmlResp = httpClient.newCall(xmlReq).execute()
                if (xmlResp.isSuccessful && xmlResp.body != null) {
                    epgStream = xmlResp.body!!.byteStream()
                    isGz = false
                }
            }

            if (epgStream != null) {
                val streamToParse = if (isGz) GZIPInputStream(epgStream) else epgStream
                val programs = XmltvParser.parse(streamToParse)
                if (programs.isNotEmpty()) {
                    programDao.insertPrograms(programs)
                    programDao.deleteOldPrograms(System.currentTimeMillis() - 6 * 3600 * 1000)
                    Log.i(TAG, "Stored ${programs.size} EPG entries")
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Channel/EPG sync failed", e)
            false
        }
    }
}
