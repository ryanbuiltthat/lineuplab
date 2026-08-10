package com.lineuplab.app.data.repository

import com.lineuplab.app.data.local.dao.SportDao
import com.lineuplab.app.data.local.dao.TeamDao
import com.lineuplab.app.data.local.entity.PlayerEntity
import com.lineuplab.app.data.local.entity.TeamEntity
import kotlinx.coroutines.flow.Flow

class TeamRepository(
    private val teamDao: TeamDao,
    private val sportDao: SportDao,
) {

    fun observeTeams(): Flow<List<TeamEntity>> = teamDao.observeTeams()

    suspend fun sportIdByName(sportName: String): Long? = sportDao.getByName(sportName)?.id

    fun observeTeam(teamId: Long): Flow<TeamEntity?> = teamDao.observeTeam(teamId)

    fun observePlayers(teamId: Long): Flow<List<PlayerEntity>> = teamDao.observePlayers(teamId)

    suspend fun createTeam(name: String, sportName: String, season: String?): Long {
        val sport = sportDao.getByName(sportName)
            ?: error("Unknown sport: $sportName")
        return teamDao.insertTeam(
            TeamEntity(name = name, sportId = sport.id, season = season)
        )
    }

    suspend fun updateTeam(team: TeamEntity) = teamDao.updateTeam(team)

    suspend fun deleteTeam(team: TeamEntity) = teamDao.deleteTeam(team)

    suspend fun addPlayer(
        teamId: Long,
        name: String,
        defaultPosition: Int,
        jerseyNumber: Int? = null,
    ): Long = teamDao.insertPlayer(
        PlayerEntity(
            teamId = teamId,
            name = name,
            defaultPosition = defaultPosition,
            jerseyNumber = jerseyNumber,
        )
    )

    suspend fun updatePlayer(player: PlayerEntity) = teamDao.updatePlayer(player)

    suspend fun deletePlayer(player: PlayerEntity) = teamDao.deletePlayer(player)
}
