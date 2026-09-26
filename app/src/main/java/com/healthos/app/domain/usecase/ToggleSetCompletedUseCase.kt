package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.ExerciseSet
import com.healthos.app.domain.repository.WorkoutRepository
import javax.inject.Inject

class ToggleSetCompletedUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) {
    suspend operator fun invoke(set: ExerciseSet) {
        workoutRepository.updateSet(set.copy(completed = !set.completed))
    }
}
