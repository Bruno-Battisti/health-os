package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.Workout
import com.healthos.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkoutHistoryUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) {
    operator fun invoke(userId: Long): Flow<List<Workout>> = workoutRepository.observeHistory(userId)
}
