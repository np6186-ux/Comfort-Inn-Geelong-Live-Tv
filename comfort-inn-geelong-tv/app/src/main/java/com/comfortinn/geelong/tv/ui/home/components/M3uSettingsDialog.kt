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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.comfortinn.geelong.tv.ui.theme.*
import com.comfortinn.geelong.tv.util.QrCodeGenerator

@Composable
fun M3uSettingsDialog(
    currentM3uUrl: String,
    serverIpUrl: String,
    onSaveM3uUrl: (String) -> Unit,
    onResetDefault: () -> Unit,
    onDismiss: () -> Unit
) {
    var inputUrl by remember(currentM3uUrl) { mutableStateOf(currentM3uUrl) }

    val qrBitmap = remember(serverIpUrl) {
        QrCodeGenerator.generateWifiQrCode(serverIpUrl, "", 220)
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(680.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(NavySurface)
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
                .padding(28.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "IPTV Settings",
                            tint = GoldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "IPTV M3U PLAYLIST CONFIGURATION",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Upload or enter any custom M3U/M3U8 stream URL for guest room TV broadcasting.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NavyCard)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (qrBitmap != null) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(4.dp)
                        ) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "Upload QR",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Scan from phone to paste M3U Link:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AmberGlow
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = serverIpUrl,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Open this link in any browser on your Wi-Fi to upload/edit instantly.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Current Active M3U URL:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = { inputUrl = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AmberGlow,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = NavyDeep,
                        unfocusedContainerColor = NavyDeep
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DialogActionButton(
                        text = "Save & Reload Channels",
                        isPrimary = true,
                        onClick = {
                            onSaveM3uUrl(inputUrl)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    )

                    DialogActionButton(
                        text = "Reset to Melbourne FTA",
                        isPrimary = false,
                        onClick = {
                            onResetDefault()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogActionButton(
    text: String,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                when {
                    isFocused -> AmberGlow
                    isPrimary -> GoldAccent
                    else -> Color(0xFF334155)
                }
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .focusable(interactionSource = interactionSource)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isFocused || isPrimary) NavyDeep else TextPrimary
        )
    }
}
