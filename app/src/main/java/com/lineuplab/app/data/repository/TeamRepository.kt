package com.lineuplab.app.data.repository

import com.lineuplab.app.data.local.dao.SportDao
import com.lineuplab.app.data.local.entity.SportEntity
import com.lineuplab.app.data.local.dao.TeamDao
import com.lineuplab.app.data.local.entity.PlayerEntity
import com.lineuplab.app.data.local.entity.TeamEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class TeamRepository(
    private val teamDao: TeamDao,
    private val sportDao: SportDao,
) {

    fun observeTeams(): Flow<List<TeamEntity>> = teamDao.observeTeams()

    fun observeTeam(teamId: Long): Flow<TeamEntity?> = teamDao.observeTeam(teamId)

    fun observePlayers(teamId: Long): Flow<List<PlayerEntity>> = teamDao.observePlayers(teamId)

    suspend fun createTeam(name: String, sportName: String, season: String?): Long {
        val sport = awaitSport(sportName)
        return teamDao.insertTeam(
            TeamEntity(name = name, sportId = sport.id, season = season)
        )
    }

    /**
     * The first-launch seeder inserts sport rows asynchronously; poll briefly
     * rather than fail a team creation that races it.
     */
    private suspend fun awaitSport(sportName: String): SportEntity {
        var sport = sportDao.getByName(sportName)
        while (sport == null) {
            delay(50)
            sport = sportDao.getByName(sportName)
        }
        return sport
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
