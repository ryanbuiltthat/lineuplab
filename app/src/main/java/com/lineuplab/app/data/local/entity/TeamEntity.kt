package com.lineuplab.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "teams",
    foreignKeys = [
        ForeignKey(
            entity = SportEntity::class,
            parentColumns = ["id"],
            childColumns = ["sport_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("sport_id")],
)
data class TeamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "sport_id")
    val sportId: Long,
    /** Optional season label, e.g. "2026 Spring". */
    val season: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)
