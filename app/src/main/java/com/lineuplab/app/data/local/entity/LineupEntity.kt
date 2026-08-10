package com.lineuplab.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A saved lineup preset: a named formation + player assignment combination
 * that can be reloaded and reused.
 */
@Entity(
    tableName = "lineups",
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
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("team_id"), Index("formation_id")],
)
data class LineupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "team_id")
    val teamId: Long,
    @ColumnInfo(name = "formation_id")
    val formationId: Long,
    val name: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)
