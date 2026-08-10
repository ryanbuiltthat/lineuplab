package com.lineuplab.app.data.local

import com.lineuplab.app.domain.model.FormationSlot
import com.lineuplab.app.domain.sport.SoccerConfig
import org.junit.Assert.assertEquals
import org.junit.Test

class FormationSlotsCodecTest {

    @Test
    fun `slots survive an encode-decode round trip`() {
        SoccerConfig.formationTemplates.forEach { template ->
            val encoded = FormationSlotsCodec.encode(template.slots)
            assertEquals(template.slots, FormationSlotsCodec.decode(encoded))
        }
    }

    @Test
    fun `decode ignores unknown keys for forward compatibility`() {
        val json = """[{"positionNumber":9,"x":0.5,"y":0.9,"futureField":"x"}]"""
        assertEquals(
            listOf(FormationSlot(9, 0.5f, 0.9f)),
            FormationSlotsCodec.decode(json),
        )
    }
}
