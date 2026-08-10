package com.lineuplab.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.lineuplab.app.domain.model.FormationType

/**
 * A formation. Standard (pre-built) formations are seeded with a null
 * [teamId] and are available to every team of that sport; custom formations
 * belong to the team that created them.
 *
 * [slotsJson] stores the ordered list of position slots as JSON, e.g.
 * `[{"positionNumber":1,"x":0.5,"y":0.06}, ...]` — the position sequence
 * plus normalized field coordinates for rendering.
 */
@Entity(
    tableName = "formations",
    foreignKeys = [
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = ["id"],
            childColumns = ["team_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = SportEntity::class,
            parentColumns = ["id"],
            childColumns = ["sport_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("team_id"), Index("sport_id")],
)
data class FormationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "team_id")
    val teamId: Long? = null,
    @ColumnInfo(name = "sport_id")
    val sportId: Long,
    val name: String,
    val type: FormationType,
    @ColumnInfo(name = "slots_json")
    val slotsJson: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)
