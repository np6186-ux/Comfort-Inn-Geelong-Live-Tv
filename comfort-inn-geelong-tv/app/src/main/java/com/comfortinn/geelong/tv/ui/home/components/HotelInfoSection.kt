package com.comfortinn.geelong.tv.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.comfortinn.geelong.tv.ui.theme.*

@Composable
fun HotelInfoSection(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(NavySurface)
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone",
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ON-CALL RECEPTION",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Call / SMS: 0452151151",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Email: reservations@comfortinngeelong.com.au",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Available 24/7 for guest assistance & check-in help.",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1.3f)
                .clip(RoundedCornerShape(16.dp))
                .background(NavySurface)
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Hotel Information",
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HOTEL & CHECK-OUT GUIDELINES",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "• Check-Out Time: 10:00 AM (Self check-out via Kiosk in lobby)",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Parking: Off-site street & council parking (No on-site parking at 151 Ryrie St)",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Policy: 100% Smoke-Free Property throughout all rooms & facilities",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
