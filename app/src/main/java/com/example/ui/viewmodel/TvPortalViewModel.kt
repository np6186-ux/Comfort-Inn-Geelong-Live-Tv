package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Channel
import com.example.data.model.Playlist
import com.example.data.model.WeatherData
import com.example.data.repository.ChannelRepository
import com.example.data.repository.WeatherRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TvPortalViewModel(application: Application) : AndroidViewModel(application) {

  private val channelRepo = ChannelRepository(application)
  private val weatherRepo = WeatherRepository()

  private val _channels = MutableStateFlow<List<Channel>>(emptyList())
  val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

  private val _currentChannel = MutableStateFlow<Channel?>(null)
  val currentChannel: StateFlow<Channel?> = _currentChannel.asStateFlow()

  private val _selectedCategory = MutableStateFlow("All")
  val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _isLoadingChannels = MutableStateFlow(false)
  val isLoadingChannels: StateFlow<Boolean> = _isLoadingChannels.asStateFlow()

  private val _statusMessage = MutableStateFlow<String?>(null)
  val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

  private val _weather = MutableStateFlow(WeatherData())
  val weather: StateFlow<WeatherData> = _weather.asStateFlow()

  private val _currentTimeString = MutableStateFlow("")
  val currentTimeString: StateFlow<String> = _currentTimeString.asStateFlow()

  private val _currentDateString = MutableStateFlow("")
  val currentDateString: StateFlow<String> = _currentDateString.asStateFlow()

  private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
  val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

  private val _activePlaylist = MutableStateFlow(channelRepo.getActivePlaylist())
  val activePlaylist: StateFlow<Playlist> = _activePlaylist.asStateFlow()

  private val _showAddPlaylistDialog = MutableStateFlow(false)
  val showAddPlaylistDialog: StateFlow<Boolean> = _showAddPlaylistDialog.asStateFlow()

  private val _showWifiDialog = MutableStateFlow(false)
  val showWifiDialog: StateFlow<Boolean> = _showWifiDialog.asStateFlow()

  private val _showReceptionDialog = MutableStateFlow(false)
  val showReceptionDialog: StateFlow<Boolean> = _showReceptionDialog.asStateFlow()

  private val _showWeatherDialog = MutableStateFlow(false)
  val showWeatherDialog: StateFlow<Boolean> = _showWeatherDialog.asStateFlow()

  private val _isFullscreenPlayer = MutableStateFlow(false)
  val isFullscreenPlayer: StateFlow<Boolean> = _isFullscreenPlayer.asStateFlow()

  private val _playerError = MutableStateFlow<String?>(null)
  val playerError: StateFlow<String?> = _playerError.asStateFlow()

  init {
    updateDateTime()
    startClockTimer()
    loadPlaylists()
    fetchWeather()
    loadChannels()
  }

  private fun startClockTimer() {
    viewModelScope.launch {
      while (isActive) {
        updateDateTime()
        delay(1000)
      }
    }
  }

  private fun updateDateTime() {
    val now = Date()
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault()).apply {
      timeZone = TimeZone.getDefault()
    }
    val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).apply {
      timeZone = TimeZone.getDefault()
    }
    _currentTimeString.value = timeFormat.format(now)
    _currentDateString.value = dateFormat.format(now)
  }

  fun fetchWeather() {
    viewModelScope.launch {
      weatherRepo.fetchGeelongWeather().onSuccess {
        _weather.value = it
      }
    }
  }

  fun loadPlaylists() {
    _playlists.value = channelRepo.getPlaylists()
    _activePlaylist.value = channelRepo.getActivePlaylist()
  }

  fun loadChannels() {
    viewModelScope.launch {
      _isLoadingChannels.value = true
      _playerError.value = null

      val result = channelRepo.fetchChannels(_activePlaylist.value.url)
      result.onSuccess { list ->
        _channels.value = list
        if (_currentChannel.value == null && list.isNotEmpty()) {
          _currentChannel.value = list.first()
        }
        _statusMessage.value = "Loaded ${list.size} live channels"
      }.onFailure {
        val cached = channelRepo.getCachedChannels()
        _channels.value = cached
        if (_currentChannel.value == null && cached.isNotEmpty()) {
          _currentChannel.value = cached.first()
        }
        _statusMessage.value = "Showing available channels"
      }
      _isLoadingChannels.value = false
    }
  }

  fun refreshChannels() {
    _statusMessage.value = "Updating channel lineup..."
    fetchWeather()
    loadChannels()
  }

  fun selectChannel(channel: Channel) {
    _playerError.value = null
    _currentChannel.value = channel
  }

  fun playNextChannel() {
    val currentList = getFilteredChannels()
    if (currentList.isEmpty()) return
    val currentIndex = currentList.indexOfFirst { it.id == _currentChannel.value?.id }
    val nextIndex = if (currentIndex != -1 && currentIndex < currentList.size - 1) {
      currentIndex + 1
    } else {
      0
    }
    selectChannel(currentList[nextIndex])
  }

  fun playPreviousChannel() {
    val currentList = getFilteredChannels()
    if (currentList.isEmpty()) return
    val currentIndex = currentList.indexOfFirst { it.id == _currentChannel.value?.id }
    val prevIndex = if (currentIndex > 0) {
      currentIndex - 1
    } else {
      currentList.size - 1
    }
    selectChannel(currentList[prevIndex])
  }

  fun selectCategory(category: String) {
    _selectedCategory.value = category
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun toggleFavorite(channel: Channel) {
    val newFav = channelRepo.toggleFavorite(channel.id)
    _channels.value = _channels.value.map {
      if (it.id == channel.id) it.copy(isFavorite = newFav) else it
    }
    if (_currentChannel.value?.id == channel.id) {
      _currentChannel.value = _currentChannel.value?.copy(isFavorite = newFav)
    }
  }

  fun addPlaylist(name: String, url: String) {
    if (url.isBlank()) return
    val newPlaylist = channelRepo.addPlaylist(name, url)
    loadPlaylists()
    switchPlaylist(newPlaylist)
    _showAddPlaylistDialog.value = false
  }

  fun switchPlaylist(playlist: Playlist) {
    channelRepo.setActivePlaylist(playlist.id)
    _activePlaylist.value = playlist
    _selectedCategory.value = "All"
    _searchQuery.value = ""
    _currentChannel.value = null
    loadChannels()
  }

  fun deletePlaylist(playlistId: String) {
    channelRepo.deletePlaylist(playlistId)
    loadPlaylists()
    loadChannels()
  }

  fun resetToDefaultPlaylist() {
    channelRepo.setActivePlaylist("default_melbourne")
    _activePlaylist.value = channelRepo.getActivePlaylist()
    loadChannels()
    _statusMessage.value = "Reset to Comfort Inn Melbourne FTA"
  }

  fun toggleFullscreen() {
    _isFullscreenPlayer.value = !_isFullscreenPlayer.value
  }

  fun setFullscreen(fullscreen: Boolean) {
    _isFullscreenPlayer.value = fullscreen
  }

  fun openWifiDialog(open: Boolean) {
    _showWifiDialog.value = open
  }

  fun openReceptionDialog(open: Boolean) {
    _showReceptionDialog.value = open
  }

  fun openWeatherDialog(open: Boolean) {
    _showWeatherDialog.value = open
  }

  fun openAddPlaylistDialog(open: Boolean) {
    _showAddPlaylistDialog.value = open
  }

  fun onPlayerError(errorMessage: String) {
    _playerError.value = errorMessage
  }

  fun clearStatusMessage() {
    _statusMessage.value = null
  }

  fun getFilteredChannels(): List<Channel> {
    val all = _channels.value
    val category = _selectedCategory.value
    val query = _searchQuery.value.trim().lowercase()

    val filteredByCategory = when (category) {
      "All" -> all
      "Favorites" -> all.filter { it.isFavorite }
      else -> all.filter { it.group.equals(category, ignoreCase = true) }
    }

    if (query.isEmpty()) return filteredByCategory

    return filteredByCategory.filter {
      it.name.lowercase().contains(query) ||
        it.channelNumber.contains(query) ||
        it.group.lowercase().contains(query)
    }
  }

  fun getCategories(): List<String> {
    val allGroups = _channels.value.map { it.group }.filter { it.isNotBlank() }.distinct()
    val list = mutableListOf("All")
    if (_channels.value.any { it.isFavorite }) {
      list.add("Favorites")
    }
    list.addAll(allGroups)
    return list
  }
}
