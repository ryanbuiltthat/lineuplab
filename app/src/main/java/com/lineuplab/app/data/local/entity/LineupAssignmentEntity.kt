package com.lineuplab.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One player-to-position assignment inside a saved lineup. Player name is
 * stored denormalized (per spec) so lineups survive roster edits.
 */
@Entity(
    tableName = "lineup_assignments",
    foreignKeys = [
        ForeignKey(
            entity = LineupEntity::class,
            parentColumns = ["id"],
            childColumns = ["lineup_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("lineup_id"),
        Index(value = ["lineup_id", "position_number"], unique = true),
    ],
)
data class LineupAssignmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "lineup_id")
    val lineupId: Long,
    @ColumnInfo(name = "player_name")
    val playerName: String,
    @ColumnInfo(name = "position_number")
    val positionNumber: Int,
)
