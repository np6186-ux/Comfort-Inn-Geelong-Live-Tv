package com.comfortinn.geelong.tv.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.comfortinn.geelong.tv.ui.theme.*
import com.comfortinn.geelong.tv.util.QrCodeGenerator

@Composable
fun WifiCard(
    ssid: String = "comfortinn geelong",
    password: String = "0452151151",
    modifier: Modifier = Modifier
) {
    val qrBitmap = remember(ssid, password) {
        QrCodeGenerator.generateWifiQrCode(ssid, password, 220)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(NavySurface)
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Wi-Fi",
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "COMPLIMENTARY WI-FI",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Network (SSID):", fontSize = 12.sp, color = TextSecondary)
                Text(
                    text = ssid,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Password:", fontSize = 12.sp, color = TextSecondary)
                Text(
                    text = password,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = AmberGlow
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Scan QR to join instantly",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (qrBitmap != null) {
                Box(
                    modifier = Modifier
                        .size(105.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(6.dp)
                ) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Wi-Fi QR Code",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
