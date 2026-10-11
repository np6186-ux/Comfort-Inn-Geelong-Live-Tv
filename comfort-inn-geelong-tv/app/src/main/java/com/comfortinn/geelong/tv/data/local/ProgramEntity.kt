package com.comfortinn.geelong.tv.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "programs",
    indices = [
        Index(value = ["channelTvgId", "startTimeEpoch"]),
        Index(value = ["channelTvgId", "endTimeEpoch"])
    ]
)
data class ProgramEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val channelTvgId: String,
    val title: String,
    val description: String?,
    val startTimeEpoch: Long,
    val endTimeEpoch: Long
)
