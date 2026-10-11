package com.comfortinn.geelong.tv.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.comfortinn.geelong.tv.R
import com.comfortinn.geelong.tv.data.remote.GeelongWeather
import com.comfortinn.geelong.tv.ui.theme.*

@Composable
fun HeaderSection(
    greeting: String,
    timeString: String,
    dateString: String,
    weather: GeelongWeather?,
    onOpenM3uSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Official Comfort Inn Logo (unaltered) + Dynamic Room Greeting
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.comfort_inn_logo),
                    contentDescription = "Comfort Inn Geelong Official Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "COMFORT INN GEELONG",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = greeting,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            }
        }

        // Right: IPTV Settings Button + Weather + Clock
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isFocused) AmberGlow else NavySurface)
                    .border(1.dp, if (isFocused) AmberGlow else Color(0xFF334155), RoundedCornerShape(12.dp))
                    .clickable(interactionSource = interactionSource, indication = null) { onOpenM3uSettings() }
                    .focusable(interactionSource = interactionSource)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "M3U Settings",
                    tint = if (isFocused) NavyDeep else GoldAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "M3U Link",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFocused) NavyDeep else TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (weather != null) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(NavySurface)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Weather",
                        tint = GoldBright,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${weather.temperatureC}°C",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Geelong • ${weather.conditionDescription}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(18.dp))
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = timeString,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = dateString,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
