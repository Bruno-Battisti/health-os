package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.HealthConnectRepository
import javax.inject.Inject

private const val DAYS_IN_WEEK = 7

class GetWeeklyExerciseSessionCountUseCase @Inject constructor(
    private val healthConnectRepository: HealthConnectRepository,
) {
    suspend operator fun invoke(): Int = healthConnectRepository.readExerciseSessionCount(DAYS_IN_WEEK)
}
