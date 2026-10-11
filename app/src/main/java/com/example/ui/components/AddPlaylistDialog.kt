package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Playlist
import com.example.data.repository.ChannelRepository
import com.example.ui.theme.ComfortGold
import com.example.ui.theme.ComfortGreen
import com.example.ui.theme.ComfortNavyBorder
import com.example.ui.theme.ComfortNavyCard
import com.example.ui.theme.ComfortNavyDark
import com.example.ui.theme.ComfortNavySurface
import com.example.ui.theme.ComfortOrange
import com.example.ui.theme.ComfortRed
import com.example.ui.theme.ComfortTextMuted

@Composable
fun AddPlaylistDialog(
  playlists: List<Playlist>,
  activePlaylist: Playlist,
  onDismiss: () -> Unit,
  onAddPlaylist: (name: String, url: String) -> Unit,
  onSwitchPlaylist: (Playlist) -> Unit,
  onDeletePlaylist: (String) -> Unit,
  onResetDefault: () -> Unit
) {
  var playlistName by remember { mutableStateOf("") }
  var playlistUrl by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = ComfortNavySurface,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, ComfortNavyBorder),
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .padding(16.dp)
        .testTag("add_playlist_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.PlaylistPlay,
              contentDescription = "Playlist Manager",
              tint = ComfortGold,
              modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Manage M3U Playlists",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Add custom live streaming links or switch feeds",
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

        // Existing Playlists List
        Text(
          text = "ACTIVE & SAVED PLAYLISTS",
          color = ComfortGold,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(playlists) { playlist ->
            val isActive = playlist.id == activePlaylist.id
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (isActive) ComfortNavyCard else ComfortNavyDark)
                .border(
                  width = if (isActive) 1.5.dp else 1.dp,
                  color = if (isActive) ComfortGold else ComfortNavyBorder,
                  shape = RoundedCornerShape(8.dp)
                )
                .clickable { onSwitchPlaylist(playlist) }
                .padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                if (isActive) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Active",
                    tint = ComfortGreen,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                }
                Column {
                  Text(
                    text = playlist.name + if (playlist.isDefault) " (Official Geelong TV)" else "",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
                  )
                  Text(
                    text = playlist.url,
                    color = ComfortTextMuted,
                    fontSize = 10.sp,
                    maxLines = 1
                  )
                }
              }

              if (!playlist.isDefault) {
                IconButton(
                  onClick = { onDeletePlaylist(playlist.id) },
                  modifier = Modifier.size(28.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = ComfortRed,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add New Playlist Form
        Text(
          text = "ADD NEW M3U / M3U8 STREAM",
          color = ComfortOrange,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = playlistName,
          onValueChange = { playlistName = it },
          label = { Text("Playlist Name (e.g. My Custom Channels)") },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = ComfortGold,
            unfocusedBorderColor = ComfortNavyBorder,
            focusedLabelColor = ComfortGold,
            unfocusedLabelColor = ComfortTextMuted
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("playlist_name_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = playlistUrl,
          onValueChange = {
            playlistUrl = it
            errorMessage = null
          },
          label = { Text("M3U / M3U8 URL (e.g. https://.../playlist.m3u8)") },
          leadingIcon = {
            Icon(Icons.Default.Link, contentDescription = "URL", tint = ComfortGold)
          },
          singleLine = true,
          isError = errorMessage != null,
          supportingText = {
            if (errorMessage != null) {
              Text(text = errorMessage!!, color = ComfortRed)
            }
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = ComfortGold,
            unfocusedBorderColor = ComfortNavyBorder,
            focusedLabelColor = ComfortGold,
            unfocusedLabelColor = ComfortTextMuted
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("playlist_url_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons: Restore Default & Add
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = {
              onResetDefault()
              onDismiss()
            },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ComfortGold),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.RestartAlt, contentDescription = "Reset", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Reset Default", fontSize = 12.sp)
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = {
                if (playlistUrl.isBlank()) {
                  errorMessage = "Please enter a valid M3U stream URL"
                } else if (!playlistUrl.startsWith("http://") && !playlistUrl.startsWith("https://")) {
                  errorMessage = "URL must start with http:// or https://"
                } else {
                  onAddPlaylist(
                    playlistName.ifBlank { "Channel List" },
                    playlistUrl.trim()
                  )
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = ComfortGold,
                contentColor = ComfortNavyDark
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("save_playlist_button")
            ) {
              Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Add & Play", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      }
    }
  }
}
