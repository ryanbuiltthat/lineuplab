package com.lineuplab.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lineuplab.app.ui.lineup.LineupBuilderScreen
import com.lineuplab.app.ui.team.TeamDetailScreen
import com.lineuplab.app.ui.team.TeamListScreen

@Composable
fun LineupLabNavHost() {
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
            )
        }
        composable(
            route = Routes.LINEUP_BUILDER,
            arguments = listOf(navArgument(Routes.LINEUP_BUILDER_ARG) { type = NavType.LongType }),
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getLong(Routes.LINEUP_BUILDER_ARG) ?: return@composable
            LineupBuilderScreen(teamId = teamId, onBack = navController::popBackStack)
        }
    }
}
