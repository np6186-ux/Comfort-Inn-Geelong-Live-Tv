package com.example.ui.components

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.example.data.model.Channel
import com.example.ui.theme.ComfortGold
import com.example.ui.theme.ComfortNavyBorder
import com.example.ui.theme.ComfortNavyCard
import com.example.ui.theme.ComfortNavyDark
import com.example.ui.theme.ComfortRed
import com.example.ui.theme.TvFocusHighlight

@OptIn(UnstableApi::class)
@Composable
fun LivePlayerView(
  channel: Channel?,
  isFullscreen: Boolean,
  onToggleFullscreen: () -> Unit,
  onNextChannel: () -> Unit,
  onPreviousChannel: () -> Unit,
  onToggleFavorite: (Channel) -> Unit,
  modifier: Modifier = Modifier,
  onError: (String) -> Unit = {}
) {
  val context = LocalContext.current

  var isBuffering by remember { mutableStateOf(true) }
  var playbackError by remember { mutableStateOf<String?>(null) }
  var showControls by remember { mutableStateOf(true) }

  // ExoPlayer instance remembered for lifecycle
  val exoPlayer = remember {
    val httpDataSourceFactory = DefaultHttpDataSource.Factory()
      .setUserAgent("otg/1.5.1 (AppleTv Apple TV 4; tvOS16.0; appletv.client)")
      .setAllowCrossProtocolRedirects(true)
      .setConnectTimeoutMs(15000)
      .setReadTimeoutMs(15000)

    val mediaSourceFactory = DefaultMediaSourceFactory(context)
      .setDataSourceFactory(httpDataSourceFactory)

    ExoPlayer.Builder(context)
      .setMediaSourceFactory(mediaSourceFactory)
      .build().apply {
        playWhenReady = true
        repeatMode = Player.REPEAT_MODE_OFF
      }
  }

  // Setup player listeners
  DisposableEffect(exoPlayer) {
    val listener = object : Player.Listener {
      override fun onPlaybackStateChanged(state: Int) {
        isBuffering = state == Player.STATE_BUFFERING
        if (state == Player.STATE_READY) {
          playbackError = null
        }
      }

      override fun onPlayerError(error: PlaybackException) {
        val msg = error.message ?: "Failed to play stream"
        playbackError = msg
        onError(msg)
      }
    }
    exoPlayer.addListener(listener)

    onDispose {
      exoPlayer.removeListener(listener)
      exoPlayer.stop()
      exoPlayer.release()
    }
  }

  // Update MediaItem when channel changes
  LaunchedEffect(channel?.id, channel?.streamUrl) {
    if (channel != null && channel.streamUrl.isNotBlank()) {
      isBuffering = true
      playbackError = null

      try {
        val userAgent = channel.userAgent ?: "otg/1.5.1 (AppleTv Apple TV 4; tvOS16.0; appletv.client)"
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
          .setUserAgent(userAgent)
          .setAllowCrossProtocolRedirects(true)
          .setConnectTimeoutMs(15000)
          .setReadTimeoutMs(15000)

        if (!channel.referrer.isNullOrBlank()) {
          httpDataSourceFactory.setDefaultRequestProperties(mapOf("Referer" to channel.referrer))
        }

        val mediaItem = MediaItem.Builder()
          .setUri(channel.streamUrl)
          .build()

        val hlsMediaSource = HlsMediaSource.Factory(httpDataSourceFactory)
          .createMediaSource(mediaItem)

        exoPlayer.setMediaSource(hlsMediaSource)
        exoPlayer.prepare()
        exoPlayer.play()
      } catch (e: Exception) {
        playbackError = e.localizedMessage ?: "Error loading stream"
      }
    } else {
      exoPlayer.stop()
    }
  }

  var isPlayerFocused by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .clip(if (isFullscreen) RoundedCornerShape(0.dp) else RoundedCornerShape(12.dp))
      .background(Color.Black)
      .border(
        width = if (isPlayerFocused && !isFullscreen) 2.5.dp else 1.dp,
        color = if (isPlayerFocused && !isFullscreen) TvFocusHighlight else ComfortNavyBorder,
        shape = if (isFullscreen) RoundedCornerShape(0.dp) else RoundedCornerShape(12.dp)
      )
      .onFocusChanged { isPlayerFocused = it.isFocused }
      .focusable()
      .clickable { showControls = !showControls }
  ) {
    if (channel != null) {
      // Surface PlayerView
      AndroidView(
        factory = { ctx ->
          PlayerView(ctx).apply {
            player = exoPlayer
            useController = false // Custom TV overlay controls
            layoutParams = FrameLayout.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT
            )
          }
        },
        update = { view ->
          view.player = exoPlayer
        },
        modifier = Modifier.fillMaxSize()
      )
    } else {
      // Placeholder when no channel selected
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Select a Free-to-Air Channel to Start Watching",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Buffering indicator
    if (isBuffering && playbackError == null) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          CircularProgressIndicator(
            color = ComfortGold,
            strokeWidth = 3.dp,
            modifier = Modifier.size(44.dp)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Buffering Live Stream...",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Playback Error Overlay
    if (playbackError != null) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(20.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = "Stream Error",
            tint = ComfortRed,
            modifier = Modifier.size(40.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Unable to connect to live broadcast",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "The network stream may be temporarily unavailable",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          TvActionButton(
            text = "Retry Stream",
            icon = Icons.Default.Refresh,
            isPrimary = true,
            onClick = {
              playbackError = null
              isBuffering = true
              exoPlayer.prepare()
              exoPlayer.play()
            },
            testTag = "retry_stream_button"
          )
        }
      }
    }

    // Channel Info Top Overlay (Visible on full screen or hover)
    if (channel != null && showControls) {
      // Top Gradient bar with Live Badge & Channel Info
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color.Black.copy(alpha = 0.8f),
                Color.Transparent
              )
            )
          )
          .padding(horizontal = 16.dp, vertical = 12.dp)
          .align(Alignment.TopCenter)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            // Live Indicator
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = ComfortRed,
              modifier = Modifier.padding(end = 10.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "LIVE",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Black
                )
              }
            }

            // Channel Number Badge
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = ComfortGold,
              modifier = Modifier.padding(end = 8.dp)
            ) {
              Text(
                text = "CH ${channel.channelNumber}",
                color = ComfortNavyDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }

            Text(
              text = channel.name,
              color = Color.White,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold
            )

            Text(
              text = " • ${channel.group}",
              color = Color.White.copy(alpha = 0.7f),
              fontSize = 13.sp
            )
          }

          // Fullscreen toggle button
          IconButton(
            onClick = onToggleFullscreen,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.5f))
              .testTag("fullscreen_toggle_button")
          ) {
            Icon(
              imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
              contentDescription = if (isFullscreen) "Exit Fullscreen" else "Fullscreen",
              tint = Color.White
            )
          }
        }
      }

      // Bottom Bar with Channel Surfing Controls
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color.Transparent,
                Color.Black.copy(alpha = 0.85f)
              )
            )
          )
          .padding(horizontal = 16.dp, vertical = 10.dp)
          .align(Alignment.BottomCenter)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Comfort Inn Geelong • In-Room TV",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            IconButton(
              onClick = onPreviousChannel,
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ComfortNavyCard)
                .testTag("prev_channel_button")
            ) {
              Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Previous Channel",
                tint = Color.White
              )
            }

            IconButton(
              onClick = { onToggleFavorite(channel) },
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ComfortNavyCard)
                .testTag("favorite_button")
            ) {
              Icon(
                imageVector = if (channel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite Channel",
                tint = if (channel.isFavorite) ComfortGold else Color.White
              )
            }

            IconButton(
              onClick = onNextChannel,
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ComfortNavyCard)
                .testTag("next_channel_button")
            ) {
              Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next Channel",
                tint = Color.White
              )
            }
          }
        }
      }
    }
  }
}
