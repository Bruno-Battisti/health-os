package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.WorkoutRepository
import javax.inject.Inject

class AddExerciseUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) {
    suspend operator fun invoke(workoutId: Long, name: String, order: Int): Long =
        workoutRepository.addExercise(workoutId, name, order)
}
