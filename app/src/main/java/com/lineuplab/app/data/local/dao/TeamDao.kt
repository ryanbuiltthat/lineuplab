package com.lineuplab.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lineuplab.app.data.local.entity.PlayerEntity
import com.lineuplab.app.data.local.entity.TeamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {

    @Insert
    suspend fun insertTeam(team: TeamEntity): Long

    @Update
    suspend fun updateTeam(team: TeamEntity)

    @Delete
    suspend fun deleteTeam(team: TeamEntity)

    @Query("SELECT * FROM teams ORDER BY created_at DESC")
    fun observeTeams(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE id = :teamId")
    fun observeTeam(teamId: Long): Flow<TeamEntity?>

    @Query("SELECT * FROM teams WHERE id = :teamId")
    suspend fun getTeam(teamId: Long): TeamEntity?

    @Insert
    suspend fun insertPlayer(player: PlayerEntity): Long

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)

    @Query("SELECT * FROM players WHERE team_id = :teamId ORDER BY default_position, name")
    fun observePlayers(teamId: Long): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :playerId")
    suspend fun getPlayer(playerId: Long): PlayerEntity?
}
