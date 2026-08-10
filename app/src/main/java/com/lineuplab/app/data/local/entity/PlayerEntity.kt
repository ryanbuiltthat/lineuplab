package com.lineuplab.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "players",
    foreignKeys = [
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = ["id"],
            childColumns = ["team_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("team_id")],
)
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "team_id")
    val teamId: Long,
    val name: String,
    /** Standard position number (1-11 for soccer). */
    @ColumnInfo(name = "default_position")
    val defaultPosition: Int,
    /** Optional jersey number (nice-to-have, nullable for MVP). */
    @ColumnInfo(name = "jersey_number")
    val jerseyNumber: Int? = null,
)
