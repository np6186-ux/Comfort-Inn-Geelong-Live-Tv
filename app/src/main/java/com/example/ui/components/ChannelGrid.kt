package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Channel
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
fun ChannelGrid(
  channels: List<Channel>,
  activeChannel: Channel?,
  categories: List<String>,
  selectedCategory: String,
  searchQuery: String,
  onCategorySelect: (String) -> Unit,
  onSearchQueryChange: (String) -> Unit,
  onChannelSelect: (Channel) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(ComfortNavyDark)
      .padding(horizontal = 16.dp, vertical = 10.dp)
  ) {
    // Categories Bar + Quick Search
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Category Filter Chips
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.weight(1f)
      ) {
        items(categories) { category ->
          val isSelected = category.equals(selectedCategory, ignoreCase = true)
          var isFocused by remember { mutableStateOf(false) }

          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) ComfortGold else ComfortNavyCard,
            modifier = Modifier
              .onFocusChanged { isFocused = it.isFocused }
              .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) TvFocusHighlight else ComfortNavyBorder,
                shape = RoundedCornerShape(20.dp)
              )
              .focusable()
              .clickable { onCategorySelect(category) }
          ) {
            Text(
              text = category,
              color = if (isSelected) ComfortNavyDark else Color.White,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(16.dp))

      // Compact Search Field
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text("Search channel / number...", fontSize = 12.sp, color = ComfortTextMuted) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = "Search", tint = ComfortTextMuted, modifier = Modifier.size(16.dp))
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = ComfortNavySurface,
          unfocusedContainerColor = ComfortNavyCard,
          focusedBorderColor = ComfortGold,
          unfocusedBorderColor = ComfortNavyBorder,
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
          .width(240.dp)
          .height(44.dp)
          .testTag("channel_search_input")
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Channels Grid
    if (channels.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(160.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.Tv,
            contentDescription = "No channels",
            tint = ComfortTextMuted,
            modifier = Modifier.size(36.dp)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "No channels match your filter",
            color = ComfortTextMuted,
            fontSize = 13.sp
          )
        }
      }
    } else {
      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 170.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(channels, key = { it.id }) { channel ->
          ChannelCard(
            channel = channel,
            isActive = channel.id == activeChannel?.id,
            onSelect = { onChannelSelect(channel) }
          )
        }
      }
    }
  }
}

@Composable
fun ChannelCard(
  channel: Channel,
  isActive: Boolean,
  onSelect: () -> Unit
) {
  var isFocused by remember { mutableStateOf(false) }

  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (isActive) ComfortNavySurface else ComfortNavyCard,
    modifier = Modifier
      .fillMaxWidth()
      .height(84.dp)
      .onFocusChanged { isFocused = it.isFocused }
      .border(
        width = if (isFocused) 3.dp else if (isActive) 2.dp else 1.dp,
        color = if (isFocused) TvFocusHighlight else if (isActive) ComfortGold else ComfortNavyBorder,
        shape = RoundedCornerShape(10.dp)
      )
      .focusable()
      .clickable { onSelect() }
      .testTag("channel_item_${channel.channelNumber}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Channel Logo / Avatar
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color.White)
          .padding(4.dp),
        contentAlignment = Alignment.Center
      ) {
        if (!channel.logoUrl.isNullOrBlank()) {
          AsyncImage(
            model = channel.logoUrl,
            contentDescription = channel.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(46.dp)
          )
        } else {
          // Fallback Initials
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(ComfortNavySurface),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = channel.name.take(2).uppercase(),
              color = ComfortGold,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(10.dp))

      // Channel Number & Title
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Channel Number Tag
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = if (isActive) ComfortGold else ComfortNavyBorder
          ) {
            Text(
              text = channel.channelNumber,
              color = if (isActive) ComfortNavyDark else Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
          }

          if (channel.isFavorite) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Default.Favorite,
              contentDescription = "Favorite",
              tint = ComfortOrange,
              modifier = Modifier.size(12.dp)
            )
          }

          if (isActive) {
            Spacer(modifier = Modifier.width(4.dp))
            Surface(
              shape = RoundedCornerShape(3.dp),
              color = ComfortGreen
            ) {
              Text(
                text = "PLAYING",
                color = Color.Black,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
          text = channel.name,
          color = Color.White,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Text(
          text = channel.group,
          color = ComfortTextMuted,
          fontSize = 10.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}
