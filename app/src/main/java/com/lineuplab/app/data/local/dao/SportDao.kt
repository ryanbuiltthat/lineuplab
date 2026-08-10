package com.lineuplab.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lineuplab.app.data.local.entity.SportEntity

@Dao
interface SportDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sport: SportEntity): Long

    @Query("SELECT * FROM sports WHERE name = :name")
    suspend fun getByName(name: String): SportEntity?

    @Query("SELECT * FROM sports")
    suspend fun getAll(): List<SportEntity>
}
