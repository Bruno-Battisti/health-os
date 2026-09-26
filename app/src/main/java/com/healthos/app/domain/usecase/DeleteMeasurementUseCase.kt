package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.MeasurementRepository
import javax.inject.Inject

class DeleteMeasurementUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository,
) {
    suspend operator fun invoke(id: Long) = measurementRepository.deleteMeasurement(id)
}
