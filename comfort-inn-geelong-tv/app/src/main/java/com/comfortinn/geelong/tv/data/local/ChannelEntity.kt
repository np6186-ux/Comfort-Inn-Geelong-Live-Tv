package com.comfortinn.geelong.tv.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey
    val tvgId: String,
    val channelNumber: Int,
    val name: String,
    val logoUrl: String?,
    val streamUrl: String,
    val groupTitle: String?
)
