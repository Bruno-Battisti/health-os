package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.GoalWithProgress
import com.healthos.app.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGoalsUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
) {
    operator fun invoke(userId: Long): Flow<List<GoalWithProgress>> = goalRepository.observeGoals(userId)
}
