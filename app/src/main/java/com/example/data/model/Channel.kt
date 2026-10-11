package com.example.data.model

data class Channel(
  val id: String,
  val name: String,
  val streamUrl: String,
  val logoUrl: String? = null,
  val channelNumber: String = "",
  val group: String = "Melbourne",
  val userAgent: String? = null,
  val referrer: String? = null,
  val isFavorite: Boolean = false
)
