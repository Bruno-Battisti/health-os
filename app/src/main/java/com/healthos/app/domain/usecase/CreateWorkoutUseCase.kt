package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.WorkoutRepository
import java.time.LocalDate
import javax.inject.Inject

class CreateWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) {
    suspend operator fun invoke(userId: Long, name: String, date: LocalDate): Long =
        workoutRepository.createWorkout(userId, name, date)
}
