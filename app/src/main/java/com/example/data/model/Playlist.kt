package com.example.data.model

data class Playlist(
  val id: String,
  val name: String,
  val url: String,
  val isDefault: Boolean = false,
  val channelCount: Int = 0
)
