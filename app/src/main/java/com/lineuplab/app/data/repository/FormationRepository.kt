package com.lineuplab.app.data.repository

import com.lineuplab.app.data.local.FormationSlotsCodec
import com.lineuplab.app.data.local.dao.FormationDao
import com.lineuplab.app.data.local.entity.FormationEntity
import com.lineuplab.app.domain.model.FormationSlot
import com.lineuplab.app.domain.model.FormationType
import kotlinx.coroutines.flow.Flow

class FormationRepository(private val formationDao: FormationDao) {

    fun observeAvailable(sportId: Long, teamId: Long): Flow<List<FormationEntity>> =
        formationDao.observeAvailable(sportId, teamId)

    fun observeStandard(sportId: Long): Flow<List<FormationEntity>> =
        formationDao.observeStandard(sportId)

    suspend fun getFormation(formationId: Long): FormationEntity? =
        formationDao.getById(formationId)

    fun slotsOf(formation: FormationEntity): List<FormationSlot> =
        FormationSlotsCodec.decode(formation.slotsJson)

    suspend fun createCustomFormation(
        teamId: Long,
        sportId: Long,
        name: String,
        slots: List<FormationSlot>,
    ): Long = formationDao.insert(
        FormationEntity(
            teamId = teamId,
            sportId = sportId,
            name = name,
            type = FormationType.CUSTOM,
            slotsJson = FormationSlotsCodec.encode(slots),
        )
    )

    suspend fun deleteFormation(formation: FormationEntity) = formationDao.delete(formation)
}
