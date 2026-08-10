package com.lineuplab.app.data.local

import com.lineuplab.app.domain.model.FormationSlot
import kotlinx.serialization.json.Json

/**
 * Encodes/decodes the ordered slot list stored in `formations.slots_json`.
 */
object FormationSlotsCodec {

    private val json = Json { ignoreUnknownKeys = true }

    fun encode(slots: List<FormationSlot>): String = json.encodeToString(slots)

    fun decode(value: String): List<FormationSlot> = json.decodeFromString(value)
}
