package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.WeatherData
import com.example.ui.theme.ComfortGold
import com.example.ui.theme.ComfortNavyBorder
import com.example.ui.theme.ComfortNavyCard
import com.example.ui.theme.ComfortNavyDark
import com.example.ui.theme.ComfortNavySurface
import com.example.ui.theme.ComfortTextMuted

@Composable
fun WeatherDetailDialog(
  weather: WeatherData,
  onRefresh: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = ComfortNavySurface,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, ComfortNavyBorder),
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .padding(16.dp)
        .testTag("weather_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Cloud,
              contentDescription = "Weather",
              tint = Color(0xFF40C4FF),
              modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Geelong Live Weather",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Victoria, Australia • Coastal Forecast",
                color = ComfortTextMuted,
                fontSize = 12.sp
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Weather Card
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ComfortNavyCard)
            .border(1.dp, ComfortNavyBorder, RoundedCornerShape(12.dp))
            .padding(18.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          Text(
            text = weather.getWeatherIconEmoji(),
            fontSize = 54.sp
          )

          Column {
            Text(
              text = "${String.format("%.1f", weather.temperature)}°C",
              color = Color.White,
              fontSize = 32.sp,
              fontWeight = FontWeight.ExtraBold
            )
            Text(
              text = weather.condition,
              color = ComfortGold,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = weather.location,
              color = ComfortTextMuted,
              fontSize = 12.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Metrics: Humidity & Wind
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(ComfortNavyDark)
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.WaterDrop, contentDescription = "Humidity", tint = Color(0xFF40C4FF), modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text("Humidity", color = ComfortTextMuted, fontSize = 11.sp)
              Text("${weather.humidity}%", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
          }

          Row(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(ComfortNavyDark)
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Air, contentDescription = "Wind", tint = ComfortGold, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text("Wind Speed", color = ComfortTextMuted, fontSize = 11.sp)
              Text("${String.format("%.1f", weather.windSpeed)} km/h", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = {
              onRefresh()
              onDismiss()
            },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ComfortGold),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Update Forecast")
          }

          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(
              containerColor = ComfortGold,
              contentColor = ComfortNavyDark
            ),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Done", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
