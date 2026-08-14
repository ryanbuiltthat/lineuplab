package com.lineuplab.app.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lineuplab.app.AppContainer
import com.lineuplab.app.data.local.entity.PlayerEntity
import com.lineuplab.app.data.local.entity.PlayingHistoryEntity
import com.lineuplab.app.data.repository.PlayingHistoryRepository
import com.lineuplab.app.data.repository.TeamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class PositionAppearance(val positionNumber: Int, val count: Int)

data class PlayerStatsSummary(
    val player: PlayerEntity,
    val totalAppearances: Int,
    val positionBreakdown: List<PositionAppearance>,
)

class PlayerStatsViewModel(
    private val teamId: Long,
    private val teamRepository: TeamRepository,
    private val playingHistoryRepository: PlayingHistoryRepository,
) : ViewModel() {

    val playerSummaries = combine(
        teamRepository.observePlayers(teamId),
        playingHistoryRepository.observeStatsForTeam(teamId),
    ) { players, stats ->
        val statsByPlayer = stats.groupBy { it.playerName }
        players.map { player ->
            val playerStats = statsByPlayer[player.name].orEmpty()
            PlayerStatsSummary(
                player = player,
                totalAppearances = playerStats.sumOf { it.totalAppearances },
                positionBreakdown = playerStats
                    .map { PositionAppearance(it.positionNumber, it.totalAppearances) }
                    .sortedByDescending { it.count },
            )
        }.sortedWith(compareByDescending<PlayerStatsSummary> { it.totalAppearances }.thenBy { it.player.name })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun recentHistory(playerName: String): Flow<List<PlayingHistoryEntity>> =
        playingHistoryRepository.observeRecentForPlayer(teamId, playerName)

    companion object {
        fun factory(teamId: Long, container: AppContainer) = viewModelFactory {
            initializer {
                PlayerStatsViewModel(
                    teamId = teamId,
                    teamRepository = container.teamRepository,
                    playingHistoryRepository = container.playingHistoryRepository,
                )
            }
        }
    }
}
