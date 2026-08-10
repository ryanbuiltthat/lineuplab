package com.lineuplab.app.data.repository

import com.lineuplab.app.data.local.dao.LineupDao
import com.lineuplab.app.data.local.dao.LineupWithAssignments
import com.lineuplab.app.data.local.entity.LineupEntity
import kotlinx.coroutines.flow.Flow

class LineupRepository(private val lineupDao: LineupDao) {

    fun observeLineups(teamId: Long): Flow<List<LineupWithAssignments>> =
        lineupDao.observeLineups(teamId)

    suspend fun getLineup(lineupId: Long): LineupWithAssignments? =
        lineupDao.getLineup(lineupId)

    /**
     * "Save as Preset": persists the lineup for reuse. Records no analytics.
     *
     * @param assignments position number -> player name
     */
    suspend fun savePreset(
        teamId: Long,
        formationId: Long,
        name: String,
        assignments: Map<Int, String>,
    ): Long = lineupDao.saveLineup(
        LineupEntity(teamId = teamId, formationId = formationId, name = name),
        assignments.map { (position, player) -> position to player },
    )

    suspend fun deleteLineup(lineup: LineupEntity) = lineupDao.deleteLineup(lineup)
}
