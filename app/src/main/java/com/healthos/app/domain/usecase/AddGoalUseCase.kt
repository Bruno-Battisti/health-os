package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.GoalRepository
import java.time.LocalDate
import javax.inject.Inject

class AddGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
) {
    suspend operator fun invoke(userId: Long, description: String, targetCount: Int, deadline: LocalDate): Long =
        goalRepository.addGoal(userId, description, targetCount, deadline)
}
