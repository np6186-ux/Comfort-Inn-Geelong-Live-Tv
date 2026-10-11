package com.comfortinn.geelong.tv.ui.player.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.comfortinn.geelong.tv.data.local.ChannelEntity
import com.comfortinn.geelong.tv.data.local.ProgramEntity
import com.comfortinn.geelong.tv.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun OsdBanner(
    visible: Boolean,
    channel: ChannelEntity?,
    currentProgram: ProgramEntity?,
    nextProgram: ProgramEntity?,
    modifier: Modifier = Modifier
) {
    val timeFormatter = SimpleDateFormat("h:mm a", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("Australia/Melbourne")
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { it },
        exit = fadeOut() + slideOutVertically { it },
        modifier = modifier
    ) {
        if (channel == null) return@AnimatedVisibility

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xE60F172A), Color(0xF2020617))
                    )
                )
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 90.dp, height = 65.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NavySurface),
                    contentAlignment = Alignment.Center
                ) {
                    if (!channel.logoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = channel.logoUrl,
                            contentDescription = channel.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        )
                    } else {
                        Text(
                            text = channel.channelNumber.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "CH ${channel.channelNumber}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberGlow
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = channel.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (currentProgram != null) {
                        val startTime = timeFormatter.format(Date(currentProgram.startTimeEpoch))
                        val endTime = timeFormatter.format(Date(currentProgram.endTimeEpoch))
                        val now = System.currentTimeMillis()
                        val duration = (currentProgram.endTimeEpoch - currentProgram.startTimeEpoch).coerceAtLeast(1)
                        val progress = ((now - currentProgram.startTimeEpoch).toFloat() / duration).coerceIn(0f, 1f)

                        Text(
                            text = "NOW: ${currentProgram.title}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$startTime - $endTime",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                color = GoldAccent,
                                trackColor = Color(0xFF334155),
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                            )
                        }
                    } else {
                        Text(
                            text = "Live Stream Transmission",
                            fontSize = 15.sp,
                            color = TextSecondary
                        )
                    }

                    if (nextProgram != null) {
                        val nextStart = timeFormatter.format(Date(nextProgram.startTimeEpoch))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "NEXT ($nextStart): ${nextProgram.title}",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}
