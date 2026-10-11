package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun HotelInfoBanner(
  timeString: String,
  dateString: String,
  weather: WeatherData,
  isLoading: Boolean,
  onRefreshClick: () -> Unit,
  onAddPlaylistClick: () -> Unit,
  onWifiClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = ComfortNavySurface,
    tonalElevation = 6.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
      // Top Row: Logo + Clock & Weather + Top Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Logo & Welcome
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          ComfortInnLogo(compact = true)

          Spacer(modifier = Modifier.width(16.dp))

          Column {
            Text(
              text = "Welcome to Comfort Inn Geelong",
              color = Color.White,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Live Free-to-Air TV & Guest Portal",
              color = ComfortGold,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Live Date & Time + Geelong Weather Badge
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // Weather Badge
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(ComfortNavyCard)
              .border(1.dp, ComfortNavyBorder, RoundedCornerShape(10.dp))
              .padding(horizontal = 14.dp, vertical = 8.dp)
          ) {
            Text(
              text = weather.getWeatherIconEmoji(),
              fontSize = 24.sp,
              modifier = Modifier.padding(end = 8.dp)
            )
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "${String.format("%.1f", weather.temperature)}°C",
                  color = Color.White,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = " • ${weather.location}",
                  color = ComfortTextMuted,
                  fontSize = 12.sp,
                  modifier = Modifier.padding(start = 4.dp)
                )
              }
              Text(
                text = "${weather.condition} • Humidity ${weather.humidity}%",
                color = ComfortTextMuted,
                fontSize = 11.sp
              )
            }
          }

          // Date & Time Box
          Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(ComfortNavyCard)
              .border(1.dp, ComfortNavyBorder, RoundedCornerShape(10.dp))
              .padding(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Text(
              text = timeString,
              color = ComfortGold,
              fontSize = 18.sp,
              fontWeight = FontWeight.ExtraBold
            )
            Text(
              text = dateString,
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }

          // Top Action Buttons: Refresh & Add Playlist
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            TvActionButton(
              text = "Refresh",
              icon = Icons.Default.Refresh,
              isLoading = isLoading,
              onClick = onRefreshClick,
              testTag = "refresh_button"
            )

            TvActionButton(
              text = "Add Playlist",
              icon = Icons.Default.Add,
              isPrimary = true,
              onClick = onAddPlaylistClick,
              testTag = "add_playlist_button"
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Bottom Info Bar: Wi-Fi Card & On-Call Reception Hotline
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Wi-Fi Info Card
        var isWifiFocused by remember { mutableStateOf(false) }
        Row(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(ComfortNavyCard)
            .border(
              width = if (isWifiFocused) 2.dp else 1.dp,
              color = if (isWifiFocused) TvFocusHighlight else ComfortNavyBorder,
              shape = RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isWifiFocused = it.isFocused }
            .focusable()
            .clickable { onWifiClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(ComfortGold.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Wifi,
              contentDescription = "Guest Wi-Fi",
              tint = ComfortGold,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "GUEST WI-FI",
              color = ComfortGold,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Network: ",
                color = ComfortTextMuted,
                fontSize = 13.sp
              )
              Text(
                text = "comfortinn geelong",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = "Password: ",
                color = ComfortTextMuted,
                fontSize = 13.sp
              )
              Text(
                text = "0452151151",
                color = ComfortGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
              )
            }
          }

          Icon(
            imageVector = Icons.Default.QrCode2,
            contentDescription = "Show QR Code",
            tint = ComfortTextMuted,
            modifier = Modifier.size(22.dp)
          )
        }

        // On-Call Reception Assistance Card
        var isReceptionFocused by remember { mutableStateOf(false) }
        Row(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(ComfortNavyCard)
            .border(
              width = if (isReceptionFocused) 2.dp else 1.dp,
              color = if (isReceptionFocused) TvFocusHighlight else ComfortNavyBorder,
              shape = RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isReceptionFocused = it.isFocused }
            .focusable()
            .padding(horizontal = 14.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(ComfortOrange.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Phone,
              contentDescription = "Reception Phone",
              tint = ComfortOrange,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = "ONCALL RECEPTION",
              color = ComfortOrange,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "If need any assistance: ",
                color = ComfortTextMuted,
                fontSize = 12.sp
              )
              Text(
                text = "0452 151 151",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun TvActionButton(
  text: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  onClick: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier,
  isPrimary: Boolean = false,
  isLoading: Boolean = false
) {
  var isFocused by remember { mutableStateOf(false) }

  Button(
    onClick = onClick,
    colors = ButtonDefaults.buttonColors(
      containerColor = if (isPrimary) ComfortGold else ComfortNavyCard,
      contentColor = if (isPrimary) ComfortNavyDark else Color.White
    ),
    shape = RoundedCornerShape(8.dp),
    modifier = modifier
      .testTag(testTag)
      .onFocusChanged { isFocused = it.isFocused }
      .border(
        width = if (isFocused) 2.5.dp else 1.dp,
        color = if (isFocused) TvFocusHighlight else if (isPrimary) ComfortGold else ComfortNavyBorder,
        shape = RoundedCornerShape(8.dp)
      )
  ) {
    if (isLoading) {
      CircularProgressIndicator(
        modifier = Modifier.size(16.dp),
        color = if (isPrimary) ComfortNavyDark else ComfortGold,
        strokeWidth = 2.dp
      )
      Spacer(modifier = Modifier.width(6.dp))
    } else {
      Icon(
        imageVector = icon,
        contentDescription = text,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
    }
    Text(
      text = text,
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold
    )
  }
}
