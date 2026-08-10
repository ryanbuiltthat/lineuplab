package com.lineuplab.app.ui.lineup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lineuplab.app.AppContainer
import com.lineuplab.app.data.local.entity.FormationEntity
import com.lineuplab.app.data.local.entity.PlayerEntity
import com.lineuplab.app.data.local.entity.TeamEntity
import com.lineuplab.app.data.repository.FormationRepository
import com.lineuplab.app.data.repository.LineupRepository
import com.lineuplab.app.data.repository.PlayingHistoryRepository
import com.lineuplab.app.data.repository.TeamRepository
import com.lineuplab.app.domain.model.FormationSlot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LineupBuilderViewModel(
    private val teamId: Long,
    private val teamRepository: TeamRepository,
    private val formationRepository: FormationRepository,
    private val lineupRepository: LineupRepository,
    private val playingHistoryRepository: PlayingHistoryRepository,
) : ViewModel() {

    val team: StateFlow<TeamEntity?> = teamRepository.observeTeam(teamId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val players: StateFlow<List<PlayerEntity>> = teamRepository.observePlayers(teamId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val availableFormations: StateFlow<List<FormationEntity>> = team
        .filterNotNull()
        .flatMapLatest { formationRepository.observeAvailable(it.sportId, teamId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _selectedFormationId = MutableStateFlow<Long?>(null)
    val selectedFormationId: StateFlow<Long?> = _selectedFormationId

    val selectedFormation: StateFlow<FormationEntity?> =
        combine(availableFormations, selectedFormationId) { formations, id ->
            formations.firstOrNull { it.id == id }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val formationSlots: StateFlow<List<FormationSlot>> = selectedFormation
        .map { formation -> formation?.let(formationRepository::slotsOf) ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _assignments = MutableStateFlow<Map<Int, String>>(emptyMap())
    val assignments: StateFlow<Map<Int, String>> = _assignments

    init {
        viewModelScope.launch {
            val firstFormation = availableFormations.filter { it.isNotEmpty() }.first().first()
            if (_selectedFormationId.value == null) {
                _selectedFormationId.value = firstFormation.id
            }
        }
    }

    fun selectFormation(formationId: Long) {
        _selectedFormationId.value = formationId
        _assignments.value = emptyMap()
    }

    fun assignPlayer(positionNumber: Int, playerName: String) {
        _assignments.update { it + (positionNumber to playerName) }
    }

    fun clearPosition(positionNumber: Int) {
        _assignments.update { it - positionNumber }
    }

    fun loadPreset(lineupId: Long) {
        viewModelScope.launch {
            val lineupWithAssignments = lineupRepository.getLineup(lineupId) ?: return@launch
            _selectedFormationId.value = lineupWithAssignments.lineup.formationId
            val assignmentsMap = lineupWithAssignments.assignments
                .associate { it.positionNumber to it.playerName }
            _assignments.value = assignmentsMap
        }
    }

    fun savePreset(name: String, onSaved: () -> Unit) {
        val formationId = _selectedFormationId.value ?: return
        viewModelScope.launch {
            lineupRepository.savePreset(teamId, formationId, name, _assignments.value)
            onSaved()
        }
    }

    fun setLineup(notes: String?, onRecorded: () -> Unit) {
        if (_assignments.value.isEmpty()) return
        viewModelScope.launch {
            playingHistoryRepository.recordLineupPlayed(
                teamId = teamId,
                formationId = _selectedFormationId.value,
                assignments = _assignments.value,
                notes = notes,
            )
            onRecorded()
        }
    }

    companion object {
        fun factory(teamId: Long, container: AppContainer) = viewModelFactory {
            initializer {
                LineupBuilderViewModel(
                    teamId = teamId,
                    teamRepository = container.teamRepository,
                    formationRepository = container.formationRepository,
                    lineupRepository = container.lineupRepository,
                    playingHistoryRepository = container.playingHistoryRepository,
                )
            }
        }
    }
}
