package com.comfortinn.geelong.tv.ui.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.comfortinn.geelong.tv.ui.theme.*
import com.comfortinn.geelong.tv.util.IntentUtil
import com.comfortinn.geelong.tv.util.StreamingApp

@Composable
fun StreamingAppsRail(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val apps = IntentUtil.POPULAR_AU_APPS

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "POPULAR STREAMING APPS",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 48.dp, vertical = 6.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(apps) { app ->
                StreamingAppItem(
                    app = app,
                    onClick = { IntentUtil.launchApp(context, app) }
                )
            }
        }
    }
}

@Composable
private fun StreamingAppItem(
    app: StreamingApp,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value
    val scale = animateFloatAsState(if (isFocused) 1.08f else 1.0f, label = "app_scale").value

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .focusable(interactionSource = interactionSource)
    ) {
        Box(
            modifier = Modifier
                .size(width = 125.dp, height = 70.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(app.primaryColor))
                .border(
                    width = if (isFocused) 3.dp else 1.dp,
                    color = if (isFocused) AmberGlow else Color.Transparent,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = app.initial,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = app.name,
            fontSize = 12.sp,
            fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
            color = if (isFocused) AmberGlow else TextPrimary
        )
    }
}
