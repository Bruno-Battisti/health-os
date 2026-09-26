package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.Measurement
import com.healthos.app.domain.model.MeasurementType
import com.healthos.app.domain.model.User
import com.healthos.app.domain.repository.MeasurementRepository
import java.time.LocalDate
import javax.inject.Inject

class AddMeasurementUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository,
) {
    suspend operator fun invoke(type: MeasurementType, value: Float, date: LocalDate) {
        measurementRepository.addMeasurement(
            Measurement(userId = User.SINGLE_USER_ID, type = type, value = value, date = date),
        )
    }
}
