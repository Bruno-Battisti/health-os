package com.healthos.app.data.repository

import com.healthos.app.data.local.dao.MeasurementDao
import com.healthos.app.data.local.entity.MeasurementEntity
import com.healthos.app.domain.model.Measurement
import com.healthos.app.domain.model.MeasurementType
import com.healthos.app.domain.repository.MeasurementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class MeasurementRepositoryImpl @Inject constructor(
    private val measurementDao: MeasurementDao,
) : MeasurementRepository {

    override suspend fun addMeasurement(measurement: Measurement) {
        measurementDao.insert(measurement.toEntity())
    }

    override suspend fun updateMeasurement(measurement: Measurement) {
        measurementDao.update(measurement.toEntity())
    }

    override suspend fun deleteMeasurement(id: Long) {
        measurementDao.delete(id)
    }

    override fun observeHistory(userId: Long, type: MeasurementType): Flow<List<Measurement>> =
        measurementDao.observeHistory(userId, type.name).map { entries -> entries.map { it.toDomain() } }
}

private fun MeasurementEntity.toDomain(): Measurement = Measurement(
    id = id,
    userId = userId,
    type = MeasurementType.valueOf(type),
    value = value,
    date = LocalDate.ofEpochDay(date),
)

private fun Measurement.toEntity(): MeasurementEntity = MeasurementEntity(
    id = id,
    userId = userId,
    type = type.name,
    value = value,
    date = date.toEpochDay(),
)
