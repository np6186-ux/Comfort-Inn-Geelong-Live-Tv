package com.comfortinn.geelong.tv.ui.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.comfortinn.geelong.tv.ui.theme.*

@Composable
fun HeroTvCard(
    onWatchLiveTvClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value
    val scale = animateFloatAsState(if (isFocused) 1.05f else 1.0f, label = "hero_scale").value

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(if (isFocused) 24.dp else 8.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (isFocused) listOf(Color(0xFF1E3A8A), Color(0xFF1D4ED8))
                    else listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                )
            )
            .border(
                width = if (isFocused) 3.dp else 1.dp,
                color = if (isFocused) AmberGlow else Color(0xFF334155),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) {
                onWatchLiveTvClick()
            }
            .focusable(interactionSource = interactionSource)
            .padding(26.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEF4444))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LIVE FREE-TO-AIR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Melbourne IPTV HD",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "WATCH LIVE TV",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ABC, SBS, 7, 9, 10 & digital guide.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(if (isFocused) AmberGlow else NavyCard),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Live TV",
                    tint = if (isFocused) NavyDeep else GoldAccent,
                    modifier = Modifier.size(38.dp)
                )
            }
        }
    }
}
