package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.HealthConnectRepository
import javax.inject.Inject

class GetTodayStepsUseCase @Inject constructor(
    private val healthConnectRepository: HealthConnectRepository,
) {
    suspend operator fun invoke(): Long = healthConnectRepository.readStepsToday()
}
