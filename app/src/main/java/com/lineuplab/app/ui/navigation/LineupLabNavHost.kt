package com.lineuplab.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lineuplab.app.AppContainer
import com.lineuplab.app.LineupLabApplication
import com.lineuplab.app.ui.lineup.LineupBuilderScreen
import com.lineuplab.app.ui.lineup.SavedLineupsScreen
import com.lineuplab.app.ui.stats.PlayerStatsScreen
import com.lineuplab.app.ui.team.TeamDetailScreen
import com.lineuplab.app.ui.team.TeamListScreen

@Composable
fun LineupLabNavHost(container: AppContainer = (LocalContext.current.applicationContext as LineupLabApplication).container) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.TEAM_LIST) {
        composable(Routes.TEAM_LIST) {
            TeamListScreen(onTeamClick = { teamId -> navController.navigate(Routes.teamDetail(teamId)) })
        }
        composable(
            route = Routes.TEAM_DETAIL,
            arguments = listOf(navArgument(Routes.TEAM_DETAIL_ARG) { type = NavType.LongType }),
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getLong(Routes.TEAM_DETAIL_ARG) ?: return@composable
            TeamDetailScreen(
                teamId = teamId,
                onBack = navController::popBackStack,
                onBuildLineup = { navController.navigate(Routes.lineupBuilder(teamId)) },
                onViewStats = { navController.navigate(Routes.playerStats(teamId)) },
            )
        }
        composable(
            route = Routes.PLAYER_STATS,
            arguments = listOf(navArgument(Routes.PLAYER_STATS_ARG) { type = NavType.LongType }),
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getLong(Routes.PLAYER_STATS_ARG) ?: return@composable
            PlayerStatsScreen(teamId = teamId, onBack = navController::popBackStack)
        }
        composable(
            route = Routes.LINEUP_BUILDER,
            arguments = listOf(
                navArgument(Routes.LINEUP_BUILDER_ARG) { type = NavType.LongType },
                navArgument(Routes.LINEUP_BUILDER_PRESET_ARG) { type = NavType.LongType; defaultValue = -1L },
            ),
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getLong(Routes.LINEUP_BUILDER_ARG) ?: return@composable
            val presetId = backStackEntry.arguments?.getLong(Routes.LINEUP_BUILDER_PRESET_ARG) ?: -1L
            LineupBuilderScreen(
                teamId = teamId,
                presetId = if (presetId > 0) presetId else null,
                container = container,
                onBack = navController::popBackStack,
                onBrowseLineups = { navController.navigate(Routes.savedLineups(teamId)) },
            )
        }
        composable(
            route = Routes.SAVED_LINEUPS,
            arguments = listOf(navArgument(Routes.SAVED_LINEUPS_ARG) { type = NavType.LongType }),
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getLong(Routes.SAVED_LINEUPS_ARG) ?: return@composable
            SavedLineupsScreen(
                teamId = teamId,
                container = container,
                onBack = navController::popBackStack,
                onPresetSelected = { lineupId ->
                    navController.navigate(Routes.lineupBuilderWithPreset(teamId, lineupId)) {
                        popUpTo(Routes.SAVED_LINEUPS) { inclusive = true }
                    }
                },
            )
        }
    }
}
