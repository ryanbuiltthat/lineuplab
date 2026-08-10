package com.lineuplab.app.data.local

import com.lineuplab.app.data.local.entity.FormationEntity
import com.lineuplab.app.data.local.entity.SportEntity
import com.lineuplab.app.domain.model.FormationType
import com.lineuplab.app.domain.sport.SportRegistry

/**
 * Seeds every registered sport and its standard formations. Runs on app
 * start; idempotent, so it also picks up formations added in app updates.
 */
class DatabaseSeeder(private val database: LineupLabDatabase) {

    suspend fun seed() {
        val sportDao = database.sportDao()
        val formationDao = database.formationDao()

        SportRegistry.all.forEach { config ->
            sportDao.insert(
                SportEntity(name = config.sportName, positionCount = config.positionCount)
            )
            val sport = sportDao.getByName(config.sportName)
                ?: error("Sport ${config.sportName} missing after seed insert")

            config.formationTemplates.forEach { template ->
                val alreadySeeded =
                    formationDao.findStandardByName(sport.id, template.name) != null
                if (!alreadySeeded) {
                    formationDao.insert(
                        FormationEntity(
                            teamId = null,
                            sportId = sport.id,
                            name = template.name,
                            type = FormationType.STANDARD,
                            slotsJson = FormationSlotsCodec.encode(template.slots),
                        )
                    )
                }
            }
        }
    }
}
