package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.GoalRepository
import javax.inject.Inject

class DeleteGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
) {
    suspend operator fun invoke(id: Long) = goalRepository.deleteGoal(id)
}
