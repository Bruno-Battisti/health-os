package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.GoalRepository
import java.time.LocalDate
import javax.inject.Inject

class CheckInGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
) {
    suspend operator fun invoke(goalId: Long, date: LocalDate) = goalRepository.checkIn(goalId, date)
}
