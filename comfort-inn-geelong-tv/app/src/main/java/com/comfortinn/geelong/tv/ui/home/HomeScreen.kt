package com.comfortinn.geelong.tv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.comfortinn.geelong.tv.ui.home.components.*
import com.comfortinn.geelong.tv.ui.theme.NavyDeep

@Composable
fun HomeScreen(
    onNavigateToPlayer: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state = viewModel.uiState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
    ) {
        HeaderSection(
            greeting = state.greeting,
            timeString = state.currentTimeString,
            dateString = state.currentDateString,
            weather = state.weather,
            onOpenM3uSettings = { viewModel.openM3uDialog() }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            HeroTvCard(
                onWatchLiveTvClick = onNavigateToPlayer,
                modifier = Modifier.weight(1.3f)
            )

            KayoHeroCard(
                modifier = Modifier.weight(1.1f)
            )

            WifiCard(
                modifier = Modifier.weight(1.0f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        StreamingAppsRail()

        Spacer(modifier = Modifier.height(16.dp))

        HotelInfoSection()

        Spacer(modifier = Modifier.height(20.dp))
    }

    if (state.isM3uDialogOpen) {
        M3uSettingsDialog(
            currentM3uUrl = state.currentM3uUrl,
            serverIpUrl = state.m3uServerIpUrl,
            onSaveM3uUrl = { viewModel.updateM3uUrl(it) },
            onResetDefault = { viewModel.resetM3uUrl() },
            onDismiss = { viewModel.closeM3uDialog() }
        )
    }
}
