package com.comfortinn.geelong.tv.ui.player

import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.ui.PlayerView
import com.comfortinn.geelong.tv.ui.player.components.EpgDrawer
import com.comfortinn.geelong.tv.ui.player.components.OfflineErrorOverlay
import com.comfortinn.geelong.tv.ui.player.components.OsdBanner
import com.comfortinn.geelong.tv.ui.player.components.ReconnectingPill

@Composable
fun LiveTvPlayerScreen(
    onNavigateBack: () -> Unit,
    viewModel: PlayerViewModel = viewModel()
) {
    val state = viewModel.uiState.collectAsState().value
    val focusRequester = remember { FocusRequester() }

    BackHandler {
        when {
            state.isEpgDrawerOpen -> viewModel.closeEpgDrawer()
            state.isOsdVisible -> viewModel.toggleOsd()
            else -> onNavigateBack()
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val currentChannel = state.channels.getOrNull(state.currentChannelIndex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_CHANNEL_UP -> {
                            viewModel.previousChannel()
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_CHANNEL_DOWN -> {
                            viewModel.nextChannel()
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            viewModel.toggleEpgDrawer()
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            viewModel.toggleOsd()
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            }
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = viewModel.exoPlayer
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (state.isReconnecting) {
            ReconnectingPill(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp)
            )
        }

        if (state.isErrorState) {
            OfflineErrorOverlay(
                onRetry = { viewModel.retryPlayback() }
            )
        }

        OsdBanner(
            visible = state.isOsdVisible && !state.isEpgDrawerOpen,
            channel = currentChannel,
            currentProgram = state.currentProgram,
            nextProgram = state.nextProgram,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        EpgDrawer(
            isOpen = state.isEpgDrawerOpen,
            channels = state.channels,
            selectedChannelIndex = state.currentChannelIndex,
            onSelectChannel = { viewModel.playChannel(it) },
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}
