package com.lineuplab.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Cached appearance counts per (team, player, position), maintained whenever
 * playing history is recorded. Derived data — can always be rebuilt from
 * [PlayingHistoryEntity].
 */
@Entity(
    tableName = "player_position_stats",
    foreignKeys = [
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = ["id"],
            childColumns = ["team_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("team_id"),
        Index(value = ["team_id", "player_name", "position_number"], unique = true),
    ],
)
data class PlayerPositionStatsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "team_id")
    val teamId: Long,
    @ColumnInfo(name = "player_name")
    val playerName: String,
    @ColumnInfo(name = "position_number")
    val positionNumber: Int,
    @ColumnInfo(name = "total_appearances")
    val totalAppearances: Int,
)
