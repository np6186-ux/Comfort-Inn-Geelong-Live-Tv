package com.comfortinn.geelong.tv.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    @Query("SELECT * FROM channels ORDER BY channelNumber ASC")
    fun getAllChannelsFlow(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels ORDER BY channelNumber ASC")
    suspend fun getAllChannels(): List<ChannelEntity>

    @Query("SELECT * FROM channels WHERE tvgId = :tvgId LIMIT 1")
    suspend fun getChannelByTvgId(tvgId: String): ChannelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<ChannelEntity>)

    @Query("DELETE FROM channels")
    suspend fun clearAll()
}
