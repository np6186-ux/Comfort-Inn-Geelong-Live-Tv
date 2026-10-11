package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeatherData
import com.example.ui.theme.ComfortGold
import com.example.ui.theme.ComfortGreen
import com.example.ui.theme.ComfortNavyBorder
import com.example.ui.theme.ComfortNavyCard
import com.example.ui.theme.ComfortNavyDark
import com.example.ui.theme.ComfortNavySurface
import com.example.ui.theme.ComfortOrange
import com.example.ui.theme.ComfortTextMuted
import com.example.ui.theme.TvFocusHighlight

data class TvHubItem(
  val id: String,
  val title: String,
  val subtitle: String,
  val value: String,
  val icon: ImageVector,
  val iconTint: Color,
  val badge: String? = null,
  val testTag: String,
  val onClick: () -> Unit
)

/**
 * TV-Optimized Grid Layout using LazyVerticalGrid specifically designed
 * for 10-foot TV viewing and Android TV D-Pad remote navigation.
 * Highlights Wi-Fi, Reception, Weather, and TV Channels.
 */
@Composable
fun TvQuickHubGrid(
  weather: WeatherData,
  channelCount: Int,
  onWifiClick: () -> Unit,
  onReceptionClick: () -> Unit,
  onWeatherClick: () -> Unit,
  onTvChannelsClick: () -> Unit,
  onRefreshClick: () -> Unit,
  onAddPlaylistClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val hubItems = remember(weather, channelCount) {
    listOf(
      TvHubItem(
        id = "wifi",
        title = "Guest Wi-Fi",
        subtitle = "comfortinn geelong",
        value = "0452151151",
        icon = Icons.Default.Wifi,
        iconTint = ComfortGold,
        badge = "FREE",
        testTag = "hub_wifi_card",
        onClick = onWifiClick
      ),
      TvHubItem(
        id = "reception",
        title = "Oncall Reception",
        subtitle = "Assistance 24/7",
        value = "0452 151 151",
        icon = Icons.Default.Phone,
        iconTint = ComfortOrange,
        badge = "ASSIST",
        testTag = "hub_reception_card",
        onClick = onReceptionClick
      ),
      TvHubItem(
        id = "weather",
        title = "Geelong Weather",
        subtitle = "${weather.condition} • ${weather.humidity}%",
        value = "${String.format("%.1f", weather.temperature)}°C",
        icon = Icons.Default.Cloud,
        iconTint = Color(0xFF40C4FF),
        badge = weather.getWeatherIconEmoji(),
        testTag = "hub_weather_card",
        onClick = onWeatherClick
      ),
      TvHubItem(
        id = "channels",
        title = "Live TV Channels",
        subtitle = "Melbourne Free-to-Air",
        value = "$channelCount Channels",
        icon = Icons.Default.Tv,
        iconTint = ComfortGreen,
        badge = "LIVE",
        testTag = "hub_channels_card",
        onClick = onTvChannelsClick
      )
    )
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
  ) {
    // Header for the TV grid
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "HOTEL SERVICES & CONTROLS",
        color = ComfortGold,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.2.sp
      )

      Text(
        text = "Navigate with TV Remote D-Pad • Press OK to select",
        color = ComfortTextMuted,
        fontSize = 10.sp
      )
    }

    LazyVerticalGrid(
      columns = GridCells.Fixed(4),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
      contentPadding = PaddingValues(2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(hubItems, key = { it.id }) { item ->
        TvHubCard(item = item)
      }
    }
  }
}

@Composable
fun TvHubCard(
  item: TvHubItem,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isFocused by interactionSource.collectIsFocusedAsState()

  // Smooth scale animation on D-pad focus
  val scale by animateFloatAsState(
    targetValue = if (isFocused) 1.04f else 1.0f,
    label = "dpad_scale"
  )

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (isFocused) ComfortNavySurface else ComfortNavyCard,
    modifier = modifier
      .fillMaxWidth()
      .height(106.dp)
      .scale(scale)
      .shadow(
        elevation = if (isFocused) 12.dp else 2.dp,
        shape = RoundedCornerShape(12.dp),
        ambientColor = if (isFocused) TvFocusHighlight else Color.Transparent,
        spotColor = if (isFocused) TvFocusHighlight else Color.Transparent
      )
      .border(
        width = if (isFocused) 3.dp else 1.dp,
        color = if (isFocused) TvFocusHighlight else ComfortNavyBorder,
        shape = RoundedCornerShape(12.dp)
      )
      .focusable(interactionSource = interactionSource)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = item.onClick
      )
      .testTag(item.testTag)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          if (isFocused) {
            Brush.verticalGradient(
              colors = listOf(
                ComfortNavySurface,
                ComfortNavySurface.copy(alpha = 0.9f)
              )
            )
          } else {
            Brush.verticalGradient(
              colors = listOf(
                ComfortNavyCard,
                ComfortNavyDark
              )
            )
          }
        )
        .padding(12.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top row: Icon + Badge / Focus Glow
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(item.iconTint.copy(alpha = if (isFocused) 0.35f else 0.18f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = item.icon,
              contentDescription = item.title,
              tint = if (isFocused) Color.White else item.iconTint,
              modifier = Modifier.size(22.dp)
            )
          }

          if (item.badge != null) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = if (isFocused) ComfortGold else ComfortNavyBorder
            ) {
              Text(
                text = item.badge,
                color = if (isFocused) ComfortNavyDark else Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Title
        Text(
          text = item.title,
          color = if (isFocused) ComfortGold else Color.White,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        // Value / Main Info
        Text(
          text = item.value,
          color = if (isFocused) Color.White else Color.White.copy(alpha = 0.9f),
          fontSize = 12.sp,
          fontWeight = FontWeight.ExtraBold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        // Subtitle
        Text(
          text = item.subtitle,
          color = ComfortTextMuted,
          fontSize = 10.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}
