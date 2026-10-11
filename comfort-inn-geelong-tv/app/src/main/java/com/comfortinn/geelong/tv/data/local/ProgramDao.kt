package com.comfortinn.geelong.tv.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramDao {
    @Query("""
        SELECT * FROM programs 
        WHERE channelTvgId = :channelTvgId 
          AND startTimeEpoch <= :currentTimeEpoch 
          AND endTimeEpoch > :currentTimeEpoch 
        LIMIT 1
    """)
    suspend fun getCurrentProgram(channelTvgId: String, currentTimeEpoch: Long): ProgramEntity?

    @Query("""
        SELECT * FROM programs 
        WHERE channelTvgId = :channelTvgId 
          AND startTimeEpoch <= :currentTimeEpoch 
          AND endTimeEpoch > :currentTimeEpoch 
        LIMIT 1
    """)
    fun getCurrentProgramFlow(channelTvgId: String, currentTimeEpoch: Long): Flow<ProgramEntity?>

    @Query("""
        SELECT * FROM programs 
        WHERE channelTvgId = :channelTvgId 
          AND startTimeEpoch >= :currentTimeEpoch 
        ORDER BY startTimeEpoch ASC 
        LIMIT 1
    """)
    suspend fun getNextProgram(channelTvgId: String, currentTimeEpoch: Long): ProgramEntity?

    @Query("""
        SELECT * FROM programs 
        WHERE channelTvgId = :channelTvgId 
          AND endTimeEpoch > :currentTimeEpoch 
        ORDER BY startTimeEpoch ASC 
        LIMIT :limit
    """)
    suspend fun getUpcomingPrograms(channelTvgId: String, currentTimeEpoch: Long, limit: Int = 10): List<ProgramEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrograms(programs: List<ProgramEntity>)

    @Query("DELETE FROM programs WHERE endTimeEpoch < :cutoffTimeEpoch")
    suspend fun deleteOldPrograms(cutoffTimeEpoch: Long)

    @Query("DELETE FROM programs")
    suspend fun clearAll()
}
