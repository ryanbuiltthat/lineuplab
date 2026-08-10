package com.lineuplab.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.lineuplab.app.LineupLabApplication
import com.lineuplab.app.data.local.entity.FormationEntity
import com.lineuplab.app.data.local.entity.TeamEntity
import com.lineuplab.app.data.repository.FormationRepository
import com.lineuplab.app.data.repository.TeamRepository
import com.lineuplab.app.domain.sport.SoccerConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class HomeUiState(
    val teams: List<TeamEntity> = emptyList(),
    val standardFormations: List<FormationEntity> = emptyList(),
)

/**
 * Placeholder home screen state: lists teams and the seeded standard soccer
 * formations. Will grow into the field/lineup navigation hub.
 */
class HomeViewModel(
    private val teamRepository: TeamRepository,
    private val formationRepository: FormationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            teamRepository.observeTeams().collectLatest { teams ->
                _uiState.value = _uiState.value.copy(teams = teams)
            }
        }
        viewModelScope.launch {
            val sportId = waitForSportId(SoccerConfig.sportName)
            formationRepository.observeStandard(sportId).collectLatest { formations ->
                _uiState.value = _uiState.value.copy(standardFormations = formations)
            }
        }
    }

    /** The seeder runs asynchronously on app start; poll briefly until the sport row exists. */
    private suspend fun waitForSportId(sportName: String): Long {
        while (true) {
            teamRepository.sportIdByName(sportName)?.let { return it }
            delay(100)
        }
    }

    val positionMappings = SoccerConfig.positionMappings

    companion object {
        val Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as LineupLabApplication
                return HomeViewModel(
                    app.container.teamRepository,
                    app.container.formationRepository,
                ) as T
            }
        }
    }
}
