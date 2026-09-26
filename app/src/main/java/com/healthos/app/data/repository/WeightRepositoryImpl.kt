package com.healthos.app.data.repository

import com.healthos.app.data.local.dao.WeightDao
import com.healthos.app.data.local.entity.WeightEntryEntity
import com.healthos.app.domain.model.WeightEntry
import com.healthos.app.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class WeightRepositoryImpl @Inject constructor(
    private val weightDao: WeightDao,
) : WeightRepository {

    override suspend fun addEntry(entry: WeightEntry) {
        weightDao.insert(entry.toEntity())
    }

    override fun observeLatest(userId: Long): Flow<WeightEntry?> =
        weightDao.observeLatest(userId).map { it?.toDomain() }
}

private fun WeightEntryEntity.toDomain(): WeightEntry = WeightEntry(
    userId = userId,
    weight = weight,
    date = LocalDate.ofEpochDay(date),
    note = note,
)

private fun WeightEntry.toEntity(): WeightEntryEntity = WeightEntryEntity(
    userId = userId,
    weight = weight,
    date = date.toEpochDay(),
    note = note,
)
