package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.Measurement
import com.healthos.app.domain.model.MeasurementType
import com.healthos.app.domain.repository.MeasurementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMeasurementHistoryUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository,
) {
    operator fun invoke(userId: Long, type: MeasurementType): Flow<List<Measurement>> =
        measurementRepository.observeHistory(userId, type)
}
