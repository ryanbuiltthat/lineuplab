package com.lineuplab.app.domain.sport

import com.lineuplab.app.domain.model.FormationSlot
import com.lineuplab.app.domain.model.PositionMapping

/**
 * A pre-built formation shipped with a sport (e.g. soccer's 4-4-2).
 */
data class FormationTemplate(
    val name: String,
    val slots: List<FormationSlot>,
)

/**
 * Sport-agnostic configuration contract. Soccer is the only implementation
 * for the MVP; basketball, hockey, etc. can be added by implementing this
 * interface and registering the sport in [SportRegistry].
 */
interface SportConfig {
    val sportName: String
    val positionCount: Int

    /** Field aspect ratio as width / length, used by the field renderer. */
    val fieldAspectRatio: Float

    val positionMappings: List<PositionMapping>
    val formationTemplates: List<FormationTemplate>

    fun mappingFor(positionNumber: Int): PositionMapping? =
        positionMappings.firstOrNull { it.number == positionNumber }
}

/**
 * Registry of supported sports, keyed by sport name as stored in the database.
 */
object SportRegistry {
    private val sports: Map<String, SportConfig> = listOf(SoccerConfig)
        .associateBy { it.sportName }

    val all: Collection<SportConfig> get() = sports.values

    fun byName(name: String): SportConfig? = sports[name]
}
