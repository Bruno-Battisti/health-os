package com.healthos.app.domain.repository

import com.healthos.app.domain.model.Measurement
import com.healthos.app.domain.model.MeasurementType
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {
    suspend fun addMeasurement(measurement: Measurement)
    suspend fun updateMeasurement(measurement: Measurement)
    suspend fun deleteMeasurement(id: Long)
    fun observeHistory(userId: Long, type: MeasurementType): Flow<List<Measurement>>
}
