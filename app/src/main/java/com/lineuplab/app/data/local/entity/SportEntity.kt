package com.lineuplab.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A sport known to the app. Position mappings and field layout are baked-in
 * constants (see SportConfig/SportRegistry) keyed by [name]; this table exists
 * so teams and formations can reference a sport by id and so new sports can
 * be added later without schema changes.
 */
@Entity(
    tableName = "sports",
    indices = [Index(value = ["name"], unique = true)],
)
data class SportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "position_count")
    val positionCount: Int,
)
