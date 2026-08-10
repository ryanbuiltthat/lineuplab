package com.lineuplab.app.domain.sport

import com.lineuplab.app.domain.model.FormationSlot
import com.lineuplab.app.domain.model.PositionMapping
import com.lineuplab.app.domain.model.RoleCategory

/**
 * Baked-in soccer configuration: the standard 1-11 position numbering and
 * the pre-built formations seeded into the database on first launch.
 *
 * Coordinate convention (see [PositionMapping]): x runs across the field
 * (0 = left touchline), y runs from own goal line (0) to opponent goal
 * line (1).
 */
object SoccerConfig : SportConfig {

    override val sportName = "Soccer"
    override val positionCount = 11

    /** A soccer pitch is roughly 68m x 105m. */
    override val fieldAspectRatio = 68f / 105f

    override val positionMappings = listOf(
        PositionMapping(1, "Goalkeeper", "GK", RoleCategory.GOALKEEPER, 0.50f, 0.06f),
        PositionMapping(2, "Right Back", "RB", RoleCategory.DEFENDER, 0.85f, 0.25f),
        PositionMapping(3, "Left Back", "LB", RoleCategory.DEFENDER, 0.15f, 0.25f),
        PositionMapping(4, "Right Center Back", "RCB", RoleCategory.DEFENDER, 0.65f, 0.18f),
        PositionMapping(5, "Left Center Back", "LCB", RoleCategory.DEFENDER, 0.35f, 0.18f),
        PositionMapping(6, "Defensive Midfielder", "CDM", RoleCategory.MIDFIELDER, 0.50f, 0.42f),
        PositionMapping(7, "Right Winger", "RW", RoleCategory.FORWARD, 0.85f, 0.78f),
        PositionMapping(8, "Center Midfielder", "CM", RoleCategory.MIDFIELDER, 0.65f, 0.55f),
        PositionMapping(9, "Forward", "ST", RoleCategory.FORWARD, 0.50f, 0.90f),
        PositionMapping(10, "Attacking Midfielder", "CAM", RoleCategory.MIDFIELDER, 0.50f, 0.68f),
        PositionMapping(11, "Left Winger", "LW", RoleCategory.FORWARD, 0.15f, 0.78f),
    )

    override val formationTemplates = listOf(
        FormationTemplate(
            name = "4-4-2",
            slots = listOf(
                FormationSlot(1, 0.50f, 0.06f),
                FormationSlot(2, 0.85f, 0.22f),
                FormationSlot(4, 0.63f, 0.18f),
                FormationSlot(5, 0.37f, 0.18f),
                FormationSlot(3, 0.15f, 0.22f),
                FormationSlot(7, 0.85f, 0.50f),
                FormationSlot(8, 0.62f, 0.46f),
                FormationSlot(6, 0.38f, 0.46f),
                FormationSlot(11, 0.15f, 0.50f),
                FormationSlot(9, 0.60f, 0.80f),
                FormationSlot(10, 0.40f, 0.78f),
            ),
        ),
        FormationTemplate(
            name = "4-3-3",
            slots = listOf(
                FormationSlot(1, 0.50f, 0.06f),
                FormationSlot(2, 0.85f, 0.22f),
                FormationSlot(4, 0.63f, 0.17f),
                FormationSlot(5, 0.37f, 0.17f),
                FormationSlot(3, 0.15f, 0.22f),
                FormationSlot(6, 0.50f, 0.42f),
                FormationSlot(8, 0.66f, 0.55f),
                FormationSlot(10, 0.34f, 0.55f),
                FormationSlot(7, 0.85f, 0.78f),
                FormationSlot(11, 0.15f, 0.78f),
                FormationSlot(9, 0.50f, 0.86f),
            ),
        ),
        FormationTemplate(
            name = "3-5-2",
            slots = listOf(
                FormationSlot(1, 0.50f, 0.06f),
                FormationSlot(4, 0.72f, 0.16f),
                FormationSlot(6, 0.50f, 0.13f),
                FormationSlot(5, 0.28f, 0.16f),
                FormationSlot(2, 0.90f, 0.50f),
                FormationSlot(3, 0.10f, 0.50f),
                FormationSlot(8, 0.62f, 0.48f),
                FormationSlot(10, 0.50f, 0.60f),
                FormationSlot(11, 0.36f, 0.48f),
                FormationSlot(7, 0.62f, 0.80f),
                FormationSlot(9, 0.40f, 0.82f),
            ),
        ),
        FormationTemplate(
            name = "4-2-3-1",
            slots = listOf(
                FormationSlot(1, 0.50f, 0.06f),
                FormationSlot(2, 0.85f, 0.22f),
                FormationSlot(4, 0.63f, 0.17f),
                FormationSlot(5, 0.37f, 0.17f),
                FormationSlot(3, 0.15f, 0.22f),
                FormationSlot(6, 0.60f, 0.40f),
                FormationSlot(8, 0.40f, 0.40f),
                FormationSlot(7, 0.85f, 0.65f),
                FormationSlot(10, 0.50f, 0.68f),
                FormationSlot(11, 0.15f, 0.65f),
                FormationSlot(9, 0.50f, 0.88f),
            ),
        ),
        FormationTemplate(
            name = "5-3-2",
            slots = listOf(
                FormationSlot(1, 0.50f, 0.06f),
                FormationSlot(2, 0.90f, 0.30f),
                FormationSlot(4, 0.68f, 0.15f),
                FormationSlot(6, 0.50f, 0.13f),
                FormationSlot(5, 0.32f, 0.15f),
                FormationSlot(3, 0.10f, 0.30f),
                FormationSlot(7, 0.72f, 0.50f),
                FormationSlot(8, 0.50f, 0.46f),
                FormationSlot(11, 0.28f, 0.50f),
                FormationSlot(10, 0.40f, 0.74f),
                FormationSlot(9, 0.60f, 0.82f),
            ),
        ),
    )
}
