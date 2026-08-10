package com.lineuplab.app.data.repository

import com.lineuplab.app.data.local.dao.PlayingHistoryDao
import com.lineuplab.app.data.local.entity.PlayerPositionStatsEntity
import com.lineuplab.app.data.local.entity.PlayingHistoryEntity
import kotlinx.coroutines.flow.Flow

class PlayingHistoryRepository(private val historyDao: PlayingHistoryDao) {

    /**
     * "Set Lineup": records that the given assignments were played and
     * updates the cached per-position appearance stats.
     *
     * @param assignments position number -> player name
     * @param dateTime epoch millis the lineup was set (defaults handled by caller)
     */
    suspend fun recordLineupPlayed(
        teamId: Long,
        formationId: Long?,
        assignments: Map<Int, String>,
        dateTime: Long = System.currentTimeMillis(),
        notes: String? = null,
    ) {
        val records = assignments.map { (position, player) ->
            PlayingHistoryEntity(
                teamId = teamId,
                playerName = player,
                positionNumber = position,
                formationId = formationId,
                dateTime = dateTime,
                notes = notes,
            )
        }
        historyDao.recordLineupPlayed(records)
    }

    fun observeStatsForPlayer(
        teamId: Long,
        playerName: String,
    ): Flow<List<PlayerPositionStatsEntity>> =
        historyDao.observeStatsForPlayer(teamId, playerName)

    fun observeStatsForTeam(teamId: Long): Flow<List<PlayerPositionStatsEntity>> =
        historyDao.observeStatsForTeam(teamId)

    fun observeRecentForPlayer(
        teamId: Long,
        playerName: String,
        limit: Int = 20,
    ): Flow<List<PlayingHistoryEntity>> =
        historyDao.observeRecentForPlayer(teamId, playerName, limit)
}
