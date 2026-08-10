package com.lineuplab.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lineuplab.app.data.local.entity.FormationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FormationDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(formation: FormationEntity): Long

    @Update
    suspend fun update(formation: FormationEntity)

    @Delete
    suspend fun delete(formation: FormationEntity)

    @Query("SELECT * FROM formations WHERE id = :formationId")
    suspend fun getById(formationId: Long): FormationEntity?

    /**
     * Formations available to a team: the sport's standard formations
     * (team_id IS NULL) plus the team's own custom formations.
     */
    @Query(
        """
        SELECT * FROM formations
        WHERE sport_id = :sportId AND (team_id IS NULL OR team_id = :teamId)
        ORDER BY type, name
        """
    )
    fun observeAvailable(sportId: Long, teamId: Long): Flow<List<FormationEntity>>

    @Query("SELECT * FROM formations WHERE team_id IS NULL AND sport_id = :sportId ORDER BY name")
    fun observeStandard(sportId: Long): Flow<List<FormationEntity>>

    @Query("SELECT COUNT(*) FROM formations WHERE team_id IS NULL AND sport_id = :sportId")
    suspend fun countStandard(sportId: Long): Int

    @Query("SELECT * FROM formations WHERE team_id IS NULL AND sport_id = :sportId AND name = :name LIMIT 1")
    suspend fun findStandardByName(sportId: Long, name: String): FormationEntity?
}
