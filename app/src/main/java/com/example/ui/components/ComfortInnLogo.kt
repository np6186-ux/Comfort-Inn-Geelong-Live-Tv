package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

/**
 * Authentic Comfort Inn Geelong Logo component matching the official 1080x1080 property asset:
 * - Solid pure white background card
 * - Signature curved ribbon 'C' emblem in warm orange (#FF6200) and golden amber (#FFB300)
 * - "Comfort™" navy wordmark
 * - "I N N" uppercase spaced subtitle
 * - "COMFORT INN GEELONG" bold property name
 */
@Composable
fun ComfortInnLogo(
  modifier: Modifier = Modifier,
  compact: Boolean = false,
  cardElevation: Dp = 4.dp
) {
  val navyColor = Color(0xFF09193D)

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(Color.White)
      .padding(horizontal = if (compact) 14.dp else 22.dp, vertical = if (compact) 8.dp else 14.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Iconic Comfort C Ribbon Emblem
      Icon(
        painter = painterResource(id = R.drawable.comfort_inn_logo),
        contentDescription = "Comfort Inn Emblem",
        tint = Color.Unspecified, // Keep vector orange & gold gradient colors
        modifier = Modifier.size(if (compact) 38.dp else 58.dp)
      )

      Spacer(modifier = Modifier.height(if (compact) 2.dp else 4.dp))

      // "Comfort™"
      Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
      ) {
        Text(
          text = "Comfort",
          color = navyColor,
          fontSize = if (compact) 18.sp else 26.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.SansSerif,
          letterSpacing = (-0.5).sp
        )
        Text(
          text = "TM",
          color = navyColor,
          fontSize = if (compact) 7.sp else 10.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.SansSerif,
          modifier = Modifier.padding(top = 1.dp, start = 1.dp)
        )
      }

      // "I N N"
      Text(
        text = "I  N  N",
        color = navyColor,
        fontSize = if (compact) 11.sp else 15.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.SansSerif,
        letterSpacing = 2.sp
      )

      Spacer(modifier = Modifier.height(if (compact) 3.dp else 6.dp))

      // "COMFORT INN GEELONG"
      Text(
        text = "COMFORT INN GEELONG",
        color = navyColor,
        fontSize = if (compact) 8.5.sp else 12.sp,
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.SansSerif,
        letterSpacing = 1.sp
      )
    }
  }
}
