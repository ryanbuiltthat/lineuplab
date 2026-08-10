package com.lineuplab.app.ui.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lineuplab.app.LineupLabApplication
import com.lineuplab.app.data.local.entity.TeamEntity
import com.lineuplab.app.data.repository.TeamRepository
import com.lineuplab.app.domain.sport.SoccerConfig
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TeamListViewModel(private val teamRepository: TeamRepository) : ViewModel() {

    val teams: StateFlow<List<TeamEntity>> = teamRepository.observeTeams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createTeam(name: String, season: String?) {
        viewModelScope.launch {
            teamRepository.createTeam(name = name, sportName = SoccerConfig.sportName, season = season)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LineupLabApplication
                TeamListViewModel(app.container.teamRepository)
            }
        }
    }
}
