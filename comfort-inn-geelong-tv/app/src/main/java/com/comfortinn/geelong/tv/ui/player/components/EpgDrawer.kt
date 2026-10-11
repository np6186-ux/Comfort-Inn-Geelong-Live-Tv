package com.comfortinn.geelong.tv.ui.player.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.comfortinn.geelong.tv.data.local.ChannelEntity
import com.comfortinn.geelong.tv.ui.theme.*

@Composable
fun EpgDrawer(
    isOpen: Boolean,
    channels: List<ChannelEntity>,
    selectedChannelIndex: Int,
    onSelectChannel: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally { -it },
        exit = slideOutHorizontally { -it },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .width(420.dp)
                .fillMaxHeight()
                .background(Color(0xF00A192F))
                .border(width = 1.dp, color = Color(0xFF334155))
                .padding(vertical = 24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "MELBOURNE CHANNELS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(channels) { index, ch ->
                        EpgChannelItem(
                            channel = ch,
                            isSelected = index == selectedChannelIndex,
                            onClick = { onSelectChannel(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EpgChannelItem(
    channel: ChannelEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                when {
                    isFocused -> AmberGlow
                    isSelected -> NavyCard
                    else -> Color.Transparent
                }
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .focusable(interactionSource = interactionSource)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(NavySurface),
            contentAlignment = Alignment.Center
        ) {
            if (!channel.logoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = channel.logoUrl,
                    contentDescription = channel.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            } else {
                Text(
                    text = channel.channelNumber.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = "${channel.channelNumber}. ${channel.name}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isFocused) NavyDeep else TextPrimary
            )
            Text(
                text = channel.groupTitle ?: "Free-to-Air",
                fontSize = 12.sp,
                color = if (isFocused) Color(0xFF1E293B) else TextSecondary
            )
        }
    }
}
