package com.comfortinn.geelong.tv.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.comfortinn.geelong.tv.data.remote.GeelongWeather
import com.comfortinn.geelong.tv.data.repository.ChannelRepository
import com.comfortinn.geelong.tv.data.repository.WeatherRepository
import com.comfortinn.geelong.tv.data.server.M3uHttpServer
import com.comfortinn.geelong.tv.util.DeviceNameUtil
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

data class HomeUiState(
    val greeting: String = "Welcome to Comfort Inn Geelong",
    val currentTimeString: String = "",
    val currentDateString: String = "",
    val weather: GeelongWeather? = null,
    val isChannelsLoaded: Boolean = false,
    val currentM3uUrl: String = "",
    val m3uServerIpUrl: String = "",
    val isM3uDialogOpen: Boolean = false
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val channelRepo = ChannelRepository(application)
    private val weatherRepo = WeatherRepository()
    private val m3uServer = M3uHttpServer(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val timeFormat = SimpleDateFormat("h:mm a", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("Australia/Melbourne")
    }
    private val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("Australia/Melbourne")
    }

    init {
        loadRoomGreeting()
        startClockTicker()
        fetchWeather()
        checkChannels()
        startM3uUploadServer()
    }

    private fun loadRoomGreeting() {
        val greeting = DeviceNameUtil.getWelcomeGreeting(getApplication())
        _uiState.value = _uiState.value.copy(
            greeting = greeting,
            currentM3uUrl = channelRepo.getM3uUrl()
        )
    }

    private fun startM3uUploadServer() {
        m3uServer.start(8080)
        val ip = m3uServer.getLocalIpAddress()
        val uploadUrl = "http://$ip:8080"
        _uiState.value = _uiState.value.copy(m3uServerIpUrl = uploadUrl)
    }

    private fun startClockTicker() {
        viewModelScope.launch {
            while (isActive) {
                val now = Date()
                _uiState.value = _uiState.value.copy(
                    currentTimeString = timeFormat.format(now),
                    currentDateString = dateFormat.format(now)
                )
                delay(1000)
            }
        }
    }

    private fun fetchWeather() {
        viewModelScope.launch {
            val weather = weatherRepo.fetchGeelongWeather()
            _uiState.value = _uiState.value.copy(weather = weather)
        }
    }

    private fun checkChannels() {
        viewModelScope.launch {
            val existing = channelRepo.getChannels()
            if (existing.isEmpty()) {
                channelRepo.refreshChannelsAndEpg()
            }
            _uiState.value = _uiState.value.copy(isChannelsLoaded = true)
        }
    }

    fun openM3uDialog() {
        _uiState.value = _uiState.value.copy(
            isM3uDialogOpen = true,
            currentM3uUrl = channelRepo.getM3uUrl()
        )
    }

    fun closeM3uDialog() {
        _uiState.value = _uiState.value.copy(isM3uDialogOpen = false)
    }

    fun updateM3uUrl(url: String) {
        channelRepo.setM3uUrl(url)
        _uiState.value = _uiState.value.copy(currentM3uUrl = url)
        viewModelScope.launch {
            channelRepo.refreshChannelsAndEpg()
        }
    }

    fun resetM3uUrl() {
        channelRepo.resetToDefaultM3uUrl()
        _uiState.value = _uiState.value.copy(currentM3uUrl = channelRepo.getM3uUrl())
        viewModelScope.launch {
            channelRepo.refreshChannelsAndEpg()
        }
    }

    override fun onCleared() {
        super.onCleared()
        m3uServer.stop()
    }
}
