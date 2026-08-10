package com.lineuplab.app.domain.sport

import com.lineuplab.app.domain.model.RoleCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SoccerConfigTest {

    @Test
    fun `position mappings cover numbers 1 through 11 exactly once`() {
        val numbers = SoccerConfig.positionMappings.map { it.number }.sorted()
        assertEquals((1..11).toList(), numbers)
    }

    @Test
    fun `position mapping labels match the standard numbering`() {
        val expected = mapOf(
            1 to "GK", 2 to "RB", 3 to "LB", 4 to "RCB", 5 to "LCB",
            6 to "CDM", 7 to "RW", 8 to "CM", 9 to "ST", 10 to "CAM", 11 to "LW",
        )
        expected.forEach { (number, abbreviation) ->
            assertEquals(abbreviation, SoccerConfig.mappingFor(number)?.abbreviation)
        }
    }

    @Test
    fun `role categories are consistent with position numbers`() {
        assertEquals(RoleCategory.GOALKEEPER, SoccerConfig.mappingFor(1)?.role)
        listOf(2, 3, 4, 5).forEach {
            assertEquals(RoleCategory.DEFENDER, SoccerConfig.mappingFor(it)?.role)
        }
        listOf(6, 8, 10).forEach {
            assertEquals(RoleCategory.MIDFIELDER, SoccerConfig.mappingFor(it)?.role)
        }
        listOf(7, 9, 11).forEach {
            assertEquals(RoleCategory.FORWARD, SoccerConfig.mappingFor(it)?.role)
        }
    }

    @Test
    fun `every formation template has 11 unique positions with valid coordinates`() {
        assertTrue(SoccerConfig.formationTemplates.isNotEmpty())
        SoccerConfig.formationTemplates.forEach { template ->
            val positions = template.slots.map { it.positionNumber }
            assertEquals(
                "${template.name} must field 11 players",
                11,
                positions.size,
            )
            assertEquals(
                "${template.name} must not repeat position numbers",
                (1..11).toList(),
                positions.sorted(),
            )
            template.slots.forEach { slot ->
                assertTrue("${template.name} x in 0..1", slot.x in 0f..1f)
                assertTrue("${template.name} y in 0..1", slot.y in 0f..1f)
                assertNotNull(SoccerConfig.mappingFor(slot.positionNumber))
            }
        }
    }

    @Test
    fun `expected standard formations are present`() {
        val names = SoccerConfig.formationTemplates.map { it.name }
        assertEquals(
            listOf("4-4-2", "4-3-3", "3-5-2", "4-2-3-1", "5-3-2"),
            names,
        )
    }
}
