package com.lineuplab.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.lineuplab.app.data.local.entity.LineupAssignmentEntity
import com.lineuplab.app.data.local.entity.LineupEntity
import kotlinx.coroutines.flow.Flow

/**
 * A lineup with its player-position assignments.
 */
data class LineupWithAssignments(
    @Embedded val lineup: LineupEntity,
    @Relation(parentColumn = "id", entityColumn = "lineup_id")
    val assignments: List<LineupAssignmentEntity>,
)

@Dao
interface LineupDao {

    @Insert
    suspend fun insertLineup(lineup: LineupEntity): Long

    @Insert
    suspend fun insertAssignments(assignments: List<LineupAssignmentEntity>)

    @Delete
    suspend fun deleteLineup(lineup: LineupEntity)

    /** Saves a lineup preset and its assignments atomically. */
    @Transaction
    suspend fun saveLineup(
        lineup: LineupEntity,
        assignments: List<Pair<Int, String>>,
    ): Long {
        val lineupId = insertLineup(lineup)
        insertAssignments(
            assignments.map { (position, playerName) ->
                LineupAssignmentEntity(
                    lineupId = lineupId,
                    playerName = playerName,
                    positionNumber = position,
                )
            }
        )
        return lineupId
    }

    @Transaction
    @Query("SELECT * FROM lineups WHERE team_id = :teamId ORDER BY created_at DESC")
    fun observeLineups(teamId: Long): Flow<List<LineupWithAssignments>>

    @Transaction
    @Query("SELECT * FROM lineups WHERE id = :lineupId")
    suspend fun getLineup(lineupId: Long): LineupWithAssignments?
}
