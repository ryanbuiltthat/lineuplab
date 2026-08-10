package com.lineuplab.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lineuplab.app.data.local.entity.PlayerPositionStatsEntity
import com.lineuplab.app.data.local.entity.PlayingHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayingHistoryDao {

    @Insert
    suspend fun insertHistory(records: List<PlayingHistoryEntity>)

    @Query(
        """
        SELECT * FROM playing_history
        WHERE team_id = :teamId AND player_name = :playerName
        ORDER BY date_time DESC
        LIMIT :limit
        """
    )
    fun observeRecentForPlayer(
        teamId: Long,
        playerName: String,
        limit: Int = 20,
    ): Flow<List<PlayingHistoryEntity>>

    @Query("SELECT * FROM playing_history WHERE team_id = :teamId ORDER BY date_time DESC")
    fun observeForTeam(teamId: Long): Flow<List<PlayingHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStatIgnore(stat: PlayerPositionStatsEntity): Long

    @Query(
        """
        UPDATE player_position_stats
        SET total_appearances = total_appearances + 1
        WHERE team_id = :teamId AND player_name = :playerName AND position_number = :positionNumber
        """
    )
    suspend fun bumpStat(teamId: Long, playerName: String, positionNumber: Int): Int

    /**
     * Portable upsert (ON CONFLICT DO UPDATE needs SQLite 3.24+, newer than
     * API 26 ships): try the increment first, insert the initial row if the
     * player/position pair wasn't tracked yet.
     */
    suspend fun incrementStat(teamId: Long, playerName: String, positionNumber: Int) {
        val updated = bumpStat(teamId, playerName, positionNumber)
        if (updated == 0) {
            insertStatIgnore(
                PlayerPositionStatsEntity(
                    teamId = teamId,
                    playerName = playerName,
                    positionNumber = positionNumber,
                    totalAppearances = 1,
                )
            )
        }
    }

    @Query(
        """
        SELECT * FROM player_position_stats
        WHERE team_id = :teamId AND player_name = :playerName
        ORDER BY position_number
        """
    )
    fun observeStatsForPlayer(
        teamId: Long,
        playerName: String,
    ): Flow<List<PlayerPositionStatsEntity>>

    @Query("SELECT * FROM player_position_stats WHERE team_id = :teamId ORDER BY player_name, position_number")
    fun observeStatsForTeam(teamId: Long): Flow<List<PlayerPositionStatsEntity>>

    /**
     * Records a set lineup: one history row per assignment plus the cached
     * stats increments, atomically.
     */
    @Transaction
    suspend fun recordLineupPlayed(records: List<PlayingHistoryEntity>) {
        insertHistory(records)
        records.forEach { record ->
            incrementStat(record.teamId, record.playerName, record.positionNumber)
        }
    }
}
