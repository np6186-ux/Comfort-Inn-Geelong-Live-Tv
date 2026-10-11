package com.comfortinn.geelong.tv.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    background = NavyDeep,
    surface = NavySurface,
    onPrimary = NavyDeep,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun ComfortInnTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
