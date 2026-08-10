package com.lineuplab.app.domain.model

/**
 * Role buckets used to group positions across sports.
 */
enum class RoleCategory {
    GOALKEEPER,
    DEFENDER,
    MIDFIELDER,
    FORWARD,
}

/**
 * Maps a standard position number to its label, abbreviation, role and
 * default location on the field.
 *
 * Coordinates are normalized percentages of the field:
 * - x: 0.0 = left touchline, 1.0 = right touchline (viewed attacking upward)
 * - y: 0.0 = own goal line, 1.0 = opponent goal line
 */
data class PositionMapping(
    val number: Int,
    val label: String,
    val abbreviation: String,
    val role: RoleCategory,
    val defaultX: Float,
    val defaultY: Float,
)
