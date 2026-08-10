package com.lineuplab.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One "player X played position Y" record, created for each assignment when
 * a coach confirms "Set Lineup". Source of truth for position analytics.
 */
@Entity(
    tableName = "playing_history",
    foreignKeys = [
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = ["id"],
            childColumns = ["team_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = FormationEntity::class,
            parentColumns = ["id"],
            childColumns = ["formation_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("team_id"), Index("formation_id"), Index("player_name")],
)
data class PlayingHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "team_id")
    val teamId: Long,
    @ColumnInfo(name = "player_name")
    val playerName: String,
    @ColumnInfo(name = "position_number")
    val positionNumber: Int,
    @ColumnInfo(name = "formation_id")
    val formationId: Long? = null,
    /** Epoch millis of when the lineup was set. */
    @ColumnInfo(name = "date_time")
    val dateTime: Long,
    /** Optional duration in minutes. */
    @ColumnInfo(name = "duration_minutes")
    val durationMinutes: Int? = null,
    val notes: String? = null,
)
