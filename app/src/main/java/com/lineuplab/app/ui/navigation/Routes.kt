package com.lineuplab.app.ui.navigation

object Routes {
    const val TEAM_LIST = "teams"
    const val TEAM_DETAIL = "teams/{teamId}"
    const val TEAM_DETAIL_ARG = "teamId"
    const val LINEUP_BUILDER = "teams/{teamId}/lineup?presetId={presetId}"
    const val LINEUP_BUILDER_ARG = "teamId"
    const val LINEUP_BUILDER_PRESET_ARG = "presetId"
    const val SAVED_LINEUPS = "teams/{teamId}/lineups"
    const val SAVED_LINEUPS_ARG = "teamId"

    fun teamDetail(teamId: Long) = "teams/$teamId"
    fun lineupBuilder(teamId: Long) = "teams/$teamId/lineup"
    fun lineupBuilderWithPreset(teamId: Long, presetId: Long) = "teams/$teamId/lineup?presetId=$presetId"
    fun savedLineups(teamId: Long) = "teams/$teamId/lineups"
}
