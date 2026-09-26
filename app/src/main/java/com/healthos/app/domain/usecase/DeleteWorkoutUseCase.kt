package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.WorkoutRepository
import javax.inject.Inject

class DeleteWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) {
    suspend operator fun invoke(id: Long) = workoutRepository.deleteWorkout(id)
}
