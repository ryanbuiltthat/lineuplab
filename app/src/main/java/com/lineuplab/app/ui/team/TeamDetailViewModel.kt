package com.lineuplab.app.ui.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lineuplab.app.data.local.entity.PlayerEntity
import com.lineuplab.app.data.local.entity.TeamEntity
import com.lineuplab.app.data.repository.TeamRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TeamDetailViewModel(
    private val teamId: Long,
    private val teamRepository: TeamRepository,
) : ViewModel() {

    val team: StateFlow<TeamEntity?> = teamRepository.observeTeam(teamId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val players: StateFlow<List<PlayerEntity>> = teamRepository.observePlayers(teamId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addPlayer(name: String, defaultPosition: Int, jerseyNumber: Int?) {
        viewModelScope.launch {
            teamRepository.addPlayer(teamId, name, defaultPosition, jerseyNumber)
        }
    }

    fun updatePlayer(player: PlayerEntity) {
        viewModelScope.launch { teamRepository.updatePlayer(player) }
    }

    fun deletePlayer(player: PlayerEntity) {
        viewModelScope.launch { teamRepository.deletePlayer(player) }
    }

    companion object {
        fun factory(teamId: Long, teamRepository: TeamRepository) = viewModelFactory {
            initializer { TeamDetailViewModel(teamId, teamRepository) }
        }
    }
}
