package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddPlaylistDialog
import com.example.ui.components.ChannelGrid
import com.example.ui.components.HotelInfoBanner
import com.example.ui.components.LivePlayerView
import com.example.ui.components.ReceptionDialog
import com.example.ui.components.TvQuickHubGrid
import com.example.ui.components.WeatherDetailDialog
import com.example.ui.components.WifiQrDialog
import com.example.ui.theme.ComfortGold
import com.example.ui.theme.ComfortNavyDark
import com.example.ui.theme.ComfortNavySurface
import com.example.ui.viewmodel.TvPortalViewModel

@Composable
fun MainTvScreen(
  viewModel: TvPortalViewModel,
  modifier: Modifier = Modifier
) {
  val channels by viewModel.channels.collectAsStateWithLifecycle()
  val activeChannel by viewModel.currentChannel.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val isLoading by viewModel.isLoadingChannels.collectAsStateWithLifecycle()
  val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()
  val weather by viewModel.weather.collectAsStateWithLifecycle()
  val timeString by viewModel.currentTimeString.collectAsStateWithLifecycle()
  val dateString by viewModel.currentDateString.collectAsStateWithLifecycle()
  val playlists by viewModel.playlists.collectAsStateWithLifecycle()
  val activePlaylist by viewModel.activePlaylist.collectAsStateWithLifecycle()
  val showAddPlaylistDialog by viewModel.showAddPlaylistDialog.collectAsStateWithLifecycle()
  val showWifiDialog by viewModel.showWifiDialog.collectAsStateWithLifecycle()
  val showReceptionDialog by viewModel.showReceptionDialog.collectAsStateWithLifecycle()
  val showWeatherDialog by viewModel.showWeatherDialog.collectAsStateWithLifecycle()
  val isFullscreen by viewModel.isFullscreenPlayer.collectAsStateWithLifecycle()

  val filteredChannels = remember(channels, selectedCategory, searchQuery) {
    viewModel.getFilteredChannels()
  }
  val categories = remember(channels) {
    viewModel.getCategories()
  }

  // Handle Android TV Remote Back Button
  BackHandler(enabled = isFullscreen) {
    viewModel.setFullscreen(false)
  }

  if (isFullscreen) {
    // Fullscreen TV Mode
    LivePlayerView(
      channel = activeChannel,
      isFullscreen = true,
      onToggleFullscreen = { viewModel.setFullscreen(false) },
      onNextChannel = { viewModel.playNextChannel() },
      onPreviousChannel = { viewModel.playPreviousChannel() },
      onToggleFavorite = { viewModel.toggleFavorite(it) },
      modifier = Modifier
        .fillMaxSize()
        .testTag("fullscreen_player_view")
    )
  } else {
    // Standard TV Dashboard Mode
    Scaffold(
      topBar = {
        HotelInfoBanner(
          timeString = timeString,
          dateString = dateString,
          weather = weather,
          isLoading = isLoading,
          onRefreshClick = { viewModel.refreshChannels() },
          onAddPlaylistClick = { viewModel.openAddPlaylistDialog(true) },
          onWifiClick = { viewModel.openWifiDialog(true) }
        )
      },
      containerColor = ComfortNavyDark,
      modifier = modifier.fillMaxSize()
    ) { innerPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        // TV-Optimized Grid Layout using LazyVerticalGrid specifically for D-pad navigation
        TvQuickHubGrid(
          weather = weather,
          channelCount = channels.size,
          onWifiClick = { viewModel.openWifiDialog(true) },
          onReceptionClick = { viewModel.openReceptionDialog(true) },
          onWeatherClick = { viewModel.openWeatherDialog(true) },
          onTvChannelsClick = {
            if (activeChannel != null) {
              viewModel.setFullscreen(true)
            }
          },
          onRefreshClick = { viewModel.refreshChannels() },
          onAddPlaylistClick = { viewModel.openAddPlaylistDialog(true) },
          modifier = Modifier.fillMaxWidth()
        )

        // Split TV Content: Live Player & Channels List
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxSize()
          ) {
            // Left Column: Live TV Video Player
            Column(
              modifier = Modifier
                .weight(0.48f)
                .fillMaxHeight()
            ) {
              LivePlayerView(
                channel = activeChannel,
                isFullscreen = false,
                onToggleFullscreen = { viewModel.setFullscreen(true) },
                onNextChannel = { viewModel.playNextChannel() },
                onPreviousChannel = { viewModel.playPreviousChannel() },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                modifier = Modifier
                  .fillMaxWidth()
                  .weight(1f)
                  .testTag("inline_live_player")
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right Column: Channel Grid & Category Tabs
            Column(
              modifier = Modifier
                .weight(0.52f)
                .fillMaxHeight()
            ) {
              ChannelGrid(
                channels = filteredChannels,
                activeChannel = activeChannel,
                categories = categories,
                selectedCategory = selectedCategory,
                searchQuery = searchQuery,
                onCategorySelect = { viewModel.selectCategory(it) },
                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                onChannelSelect = { viewModel.selectChannel(it) },
                modifier = Modifier.fillMaxSize()
              )
            }
          }

          // Status Toast Overlay
          if (statusMessage != null) {
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = ComfortNavySurface,
              border = androidx.compose.foundation.BorderStroke(1.dp, ComfortGold),
              modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
            ) {
              Text(
                text = statusMessage ?: "",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
              )
            }
          }
        }
      }
    }
  }

  // Dialogs
  if (showAddPlaylistDialog) {
    AddPlaylistDialog(
      playlists = playlists,
      activePlaylist = activePlaylist,
      onDismiss = { viewModel.openAddPlaylistDialog(false) },
      onAddPlaylist = { name, url -> viewModel.addPlaylist(name, url) },
      onSwitchPlaylist = { viewModel.switchPlaylist(it) },
      onDeletePlaylist = { viewModel.deletePlaylist(it) },
      onResetDefault = { viewModel.resetToDefaultPlaylist() }
    )
  }

  if (showWifiDialog) {
    WifiQrDialog(
      onDismiss = { viewModel.openWifiDialog(false) }
    )
  }

  if (showReceptionDialog) {
    ReceptionDialog(
      onDismiss = { viewModel.openReceptionDialog(false) }
    )
  }

  if (showWeatherDialog) {
    WeatherDetailDialog(
      weather = weather,
      onRefresh = { viewModel.fetchWeather() },
      onDismiss = { viewModel.openWeatherDialog(false) }
    )
  }
}
