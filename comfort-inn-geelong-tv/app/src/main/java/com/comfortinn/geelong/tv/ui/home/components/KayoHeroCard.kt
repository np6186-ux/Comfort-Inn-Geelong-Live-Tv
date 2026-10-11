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
import androidx.compose.material.icons.filled.SportsScore
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.comfortinn.geelong.tv.ui.theme.*
import com.comfortinn.geelong.tv.util.IntentUtil

@Composable
fun KayoHeroCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value
    val scale = animateFloatAsState(if (isFocused) 1.05f else 1.0f, label = "kayo_scale").value

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(if (isFocused) 20.dp else 6.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (isFocused) listOf(Color(0xFF007A37), Color(0xFF00B050))
                    else listOf(Color(0xFF082216), Color(0xFF0E3824))
                )
            )
            .border(
                width = if (isFocused) 3.dp else 1.dp,
                color = if (isFocused) Color(0xFF00FF77) else Color(0xFF1E5638),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) {
                IntentUtil.launchKayo(context)
            }
            .focusable(interactionSource = interactionSource)
            .padding(24.dp)
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
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF00B050))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "LIVE & ON DEMAND",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "AFL • NRL • Cricket",
                        fontSize = 12.sp,
                        color = Color(0xFFA7F3D0)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "KAYO SPORTS",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Stream 50+ live sports in HD.",
                    fontSize = 13.sp,
                    color = Color(0xFFD1FAE5)
                )
            }

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(if (isFocused) Color.White else Color(0xFF00B050)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsScore,
                    contentDescription = "Kayo Sports",
                    tint = if (isFocused) Color(0xFF00B050) else Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
