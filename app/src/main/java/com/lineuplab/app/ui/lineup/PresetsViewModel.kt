package com.lineuplab.app.ui.lineup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lineuplab.app.AppContainer
import com.lineuplab.app.data.repository.FormationRepository
import com.lineuplab.app.data.repository.LineupRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PresetLineupUi(
    val lineupId: Long,
    val name: String,
    val formationName: String,
    val playerCount: Int,
    val createdAtMs: Long,
)

class PresetsViewModel(
    private val teamId: Long,
    private val lineupRepository: LineupRepository,
    private val formationRepository: FormationRepository,
) : ViewModel() {

    val lineups = lineupRepository.observeLineups(teamId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun deletePreset(lineupId: Long) {
        viewModelScope.launch {
            val lineup = lineupRepository.getLineup(lineupId) ?: return@launch
            lineupRepository.deleteLineup(lineup.lineup)
        }
    }

    companion object {
        fun factory(teamId: Long, container: AppContainer) = viewModelFactory {
            initializer {
                PresetsViewModel(
                    teamId = teamId,
                    lineupRepository = container.lineupRepository,
                    formationRepository = container.formationRepository,
                )
            }
        }
    }
}
