package com.lineuplab.app.domain.model

import kotlinx.serialization.Serializable

/**
 * A single slot in a formation: which position number occupies it and where
 * it sits on the field (normalized 0..1 coordinates, see [PositionMapping]).
 *
 * Serialized to JSON for storage in the formations table.
 */
@Serializable
data class FormationSlot(
    val positionNumber: Int,
    val x: Float,
    val y: Float,
)
