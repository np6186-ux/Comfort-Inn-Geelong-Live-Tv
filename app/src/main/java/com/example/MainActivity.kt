package com.example

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.screens.MainTvScreen
import com.example.ui.theme.ComfortNavyDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TvPortalViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: TvPortalViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Keep screen on for continuous TV viewing in hotel rooms
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = ComfortNavyDark
        ) {
          MainTvScreen(viewModel = viewModel)
        }
      }
    }
  }

  // Handle hardware Android TV remote control keys
  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    return when (keyCode) {
      KeyEvent.KEYCODE_CHANNEL_UP, KeyEvent.KEYCODE_MEDIA_NEXT -> {
        viewModel.playNextChannel()
        true
      }
      KeyEvent.KEYCODE_CHANNEL_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
        viewModel.playPreviousChannel()
        true
      }
      KeyEvent.KEYCODE_BACK -> {
        if (viewModel.isFullscreenPlayer.value) {
          viewModel.setFullscreen(false)
          true
        } else {
          super.onKeyDown(keyCode, event)
        }
      }
      else -> super.onKeyDown(keyCode, event)
    }
  }
}
