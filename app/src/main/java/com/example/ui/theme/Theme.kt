package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ComfortInnColorScheme = darkColorScheme(
  primary = ComfortGold,
  onPrimary = ComfortNavyDark,
  primaryContainer = ComfortNavyCard,
  onPrimaryContainer = ComfortWhite,
  secondary = ComfortOrange,
  onSecondary = ComfortWhite,
  secondaryContainer = ComfortNavyBorder,
  onSecondaryContainer = ComfortWhite,
  tertiary = ComfortGoldDark,
  onTertiary = ComfortNavyDark,
  background = ComfortNavyDark,
  onBackground = ComfortWhite,
  surface = ComfortNavySurface,
  onSurface = ComfortWhite,
  surfaceVariant = ComfortNavyCard,
  onSurfaceVariant = ComfortTextMuted,
  outline = ComfortNavyBorder,
  outlineVariant = ComfortNavyCard,
  error = ComfortRed,
  onError = ComfortWhite,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use strict Comfort Inn brand colors
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = ComfortInnColorScheme,
    typography = Typography,
    content = content
  )
}
