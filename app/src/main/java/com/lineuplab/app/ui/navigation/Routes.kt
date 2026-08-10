package com.lineuplab.app.ui.navigation

object Routes {
    const val TEAM_LIST = "teams"
    const val TEAM_DETAIL = "teams/{teamId}"
    const val TEAM_DETAIL_ARG = "teamId"
    const val LINEUP_BUILDER = "teams/{teamId}/lineup"
    const val LINEUP_BUILDER_ARG = "teamId"

    fun teamDetail(teamId: Long) = "teams/$teamId"
    fun lineupBuilder(teamId: Long) = "teams/$teamId/lineup"
}
