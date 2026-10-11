package com.comfortinn.geelong.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.comfortinn.geelong.tv.ui.home.HomeScreen
import com.comfortinn.geelong.tv.ui.player.LiveTvPlayerScreen
import com.comfortinn.geelong.tv.ui.theme.ComfortInnTheme

enum class Screen {
    HOME,
    PLAYER
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComfortInnTheme {
                var currentScreen by remember { mutableStateOf(Screen.HOME) }

                when (currentScreen) {
                    Screen.HOME -> {
                        HomeScreen(
                            onNavigateToPlayer = { currentScreen = Screen.PLAYER }
                        )
                    }
                    Screen.PLAYER -> {
                        LiveTvPlayerScreen(
                            onNavigateBack = { currentScreen = Screen.HOME }
                        )
                    }
                }
            }
        }
    }
}
