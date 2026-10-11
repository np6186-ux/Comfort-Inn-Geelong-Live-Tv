package com.comfortinn.geelong.tv.ui.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import com.comfortinn.geelong.tv.data.local.ChannelEntity
import com.comfortinn.geelong.tv.data.local.ProgramEntity
import com.comfortinn.geelong.tv.data.repository.ChannelRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlayerUiState(
    val channels: List<ChannelEntity> = emptyList(),
    val currentChannelIndex: Int = 0,
    val currentProgram: ProgramEntity? = null,
    val nextProgram: ProgramEntity? = null,
    val isOsdVisible: Boolean = true,
    val isEpgDrawerOpen: Boolean = false,
    val isReconnecting: Boolean = false,
    val isErrorState: Boolean = false
)

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ChannelRepository(application)
    val exoPlayer: ExoPlayer

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var osdHideJob: Job? = null
    private var retryCount = 0

    init {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs = */ 1500,
                /* maxBufferMs = */ 5000,
                /* bufferForPlaybackMs = */ 500,
                /* bufferForPlaybackAfterRebufferMs = */ 1000
            )
            .build()

        exoPlayer = ExoPlayer.Builder(application)
            .setLoadControl(loadControl)
            .build()

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    retryCount = 0
                    _uiState.value = _uiState.value.copy(
                        isReconnecting = false,
                        isErrorState = false
                    )
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                handlePlaybackError()
            }
        })

        loadChannels()
    }

    private fun loadChannels() {
        viewModelScope.launch {
            val list = repo.getChannels()
            if (list.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(channels = list, currentChannelIndex = 0)
                playChannel(0)
            } else {
                repo.refreshChannelsAndEpg()
                val refreshed = repo.getChannels()
                _uiState.value = _uiState.value.copy(channels = refreshed, currentChannelIndex = 0)
                if (refreshed.isNotEmpty()) {
                    playChannel(0)
                }
            }
        }
    }

    fun playChannel(index: Int) {
        val channels = _uiState.value.channels
        if (index !in channels.indices) return

        val target = channels[index]
        _uiState.value = _uiState.value.copy(
            currentChannelIndex = index,
            isEpgDrawerOpen = false
        )

        val mediaItem = MediaItem.Builder()
            .setUri(target.streamUrl)
            .setLiveConfiguration(
                MediaItem.LiveConfiguration.Builder()
                    .setTargetOffsetMs(3000)
                    .build()
            )
            .build()

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        showOsd()
        updateEpgForChannel(target.tvgId)
    }

    fun nextChannel() {
        val total = _uiState.value.channels.size
        if (total == 0) return
        val nextIdx = (_uiState.value.currentChannelIndex + 1) % total
        playChannel(nextIdx)
    }

    fun previousChannel() {
        val total = _uiState.value.channels.size
        if (total == 0) return
        val prevIdx = if (_uiState.value.currentChannelIndex - 1 < 0) total - 1 else _uiState.value.currentChannelIndex - 1
        playChannel(prevIdx)
    }

    fun toggleOsd() {
        if (_uiState.value.isOsdVisible) {
            _uiState.value = _uiState.value.copy(isOsdVisible = false)
        } else {
            showOsd()
        }
    }

    fun showOsd() {
        _uiState.value = _uiState.value.copy(isOsdVisible = true)
        osdHideJob?.cancel()
        osdHideJob = viewModelScope.launch {
            delay(5000)
            _uiState.value = _uiState.value.copy(isOsdVisible = false)
        }
    }

    fun toggleEpgDrawer() {
        val next = !_uiState.value.isEpgDrawerOpen
        _uiState.value = _uiState.value.copy(isEpgDrawerOpen = next)
        if (next) {
            _uiState.value = _uiState.value.copy(isOsdVisible = false)
        }
    }

    fun closeEpgDrawer() {
        _uiState.value = _uiState.value.copy(isEpgDrawerOpen = false)
    }

    private fun updateEpgForChannel(tvgId: String) {
        viewModelScope.launch {
            val curr = repo.getCurrentProgram(tvgId)
            val next = repo.getNextProgram(tvgId)
            _uiState.value = _uiState.value.copy(
                currentProgram = curr,
                nextProgram = next
            )
        }
    }

    private fun handlePlaybackError() {
        if (retryCount < 4) {
            retryCount++
            _uiState.value = _uiState.value.copy(isReconnecting = true)
            viewModelScope.launch {
                delay((1000L * retryCount))
                exoPlayer.prepare()
            }
        } else {
            _uiState.value = _uiState.value.copy(
                isReconnecting = false,
                isErrorState = true
            )
        }
    }

    fun retryPlayback() {
        retryCount = 0
        _uiState.value = _uiState.value.copy(isErrorState = false, isReconnecting = true)
        exoPlayer.prepare()
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}
