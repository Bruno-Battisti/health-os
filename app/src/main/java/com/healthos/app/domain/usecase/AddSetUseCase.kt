package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.WorkoutRepository
import javax.inject.Inject

class AddSetUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) {
    suspend operator fun invoke(exerciseId: Long, weight: Float, repetitions: Int, order: Int): Long =
        workoutRepository.addSet(exerciseId, weight, repetitions, order)
}
