package com.healthos.app.domain.repository

import com.healthos.app.domain.model.ExerciseSet
import com.healthos.app.domain.model.Workout
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface WorkoutRepository {
    suspend fun createWorkout(userId: Long, name: String, date: LocalDate): Long
    suspend fun deleteWorkout(id: Long)
    suspend fun finishWorkout(workoutId: Long, durationMinutes: Int)

    suspend fun addExercise(workoutId: Long, name: String, order: Int): Long

    suspend fun addSet(exerciseId: Long, weight: Float, repetitions: Int, order: Int): Long
    suspend fun updateSet(set: ExerciseSet)

    fun observeHistory(userId: Long): Flow<List<Workout>>
    fun observeWorkout(workoutId: Long): Flow<Workout?>
}
