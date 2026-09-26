package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.WorkoutRepository
import javax.inject.Inject

class FinishWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) {
    suspend operator fun invoke(workoutId: Long, durationMinutes: Int) =
        workoutRepository.finishWorkout(workoutId, durationMinutes)
}
