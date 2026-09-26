package com.healthos.app.data.repository

import com.healthos.app.data.local.dao.ExerciseDao
import com.healthos.app.data.local.dao.ExerciseSetDao
import com.healthos.app.data.local.dao.WorkoutDao
import com.healthos.app.data.local.entity.ExerciseEntity
import com.healthos.app.data.local.entity.ExerciseSetEntity
import com.healthos.app.data.local.entity.WorkoutEntity
import com.healthos.app.domain.model.Exercise
import com.healthos.app.domain.model.ExerciseSet
import com.healthos.app.domain.model.Workout
import com.healthos.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao,
    private val exerciseSetDao: ExerciseSetDao,
) : WorkoutRepository {

    override suspend fun createWorkout(userId: Long, name: String, date: LocalDate): Long =
        workoutDao.insert(WorkoutEntity(userId = userId, name = name, date = date.toEpochDay()))

    override suspend fun deleteWorkout(id: Long) {
        workoutDao.delete(id)
    }

    override suspend fun finishWorkout(workoutId: Long, durationMinutes: Int) {
        val workout = workoutDao.observeById(workoutId).first() ?: return
        workoutDao.update(workout.copy(durationMinutes = durationMinutes))
    }

    override suspend fun addExercise(workoutId: Long, name: String, order: Int): Long =
        exerciseDao.insert(ExerciseEntity(workoutId = workoutId, name = name, order = order))

    override suspend fun addSet(exerciseId: Long, weight: Float, repetitions: Int, order: Int): Long =
        exerciseSetDao.insert(
            ExerciseSetEntity(exerciseId = exerciseId, weight = weight, repetitions = repetitions, completed = false, order = order),
        )

    override suspend fun updateSet(set: ExerciseSet) {
        exerciseSetDao.update(set.toEntity())
    }

    override fun observeHistory(userId: Long): Flow<List<Workout>> =
        workoutDao.observeHistory(userId).map { workouts -> workouts.map { it.toDomain(emptyList()) } }

    override fun observeWorkout(workoutId: Long): Flow<Workout?> =
        workoutDao.observeById(workoutId).flatMapLatest { workoutEntity ->
            if (workoutEntity == null) {
                flowOf(null)
            } else {
                exerciseDao.observeByWorkout(workoutId).flatMapLatest { exercises ->
                    buildExerciseTrees(exercises).map { fullExercises -> workoutEntity.toDomain(fullExercises) }
                }
            }
        }

    private fun buildExerciseTrees(exercises: List<ExerciseEntity>): Flow<List<Exercise>> {
        if (exercises.isEmpty()) return flowOf(emptyList())

        return exerciseSetDao.observeByExercises(exercises.map { it.id }).map { sets ->
            val setsByExercise = sets.groupBy { it.exerciseId }
            exercises.map { exercise -> exercise.toDomain(setsByExercise[exercise.id].orEmpty().map { it.toDomain() }) }
        }
    }
}

private fun WorkoutEntity.toDomain(exercises: List<Exercise>): Workout = Workout(
    id = id,
    userId = userId,
    name = name,
    date = LocalDate.ofEpochDay(date),
    durationMinutes = durationMinutes,
    exercises = exercises,
)

private fun ExerciseEntity.toDomain(sets: List<ExerciseSet>): Exercise = Exercise(
    id = id,
    workoutId = workoutId,
    name = name,
    order = order,
    sets = sets,
)

private fun ExerciseSetEntity.toDomain(): ExerciseSet = ExerciseSet(
    id = id,
    exerciseId = exerciseId,
    weight = weight,
    repetitions = repetitions,
    completed = completed,
    order = order,
)

private fun ExerciseSet.toEntity(): ExerciseSetEntity = ExerciseSetEntity(
    id = id,
    exerciseId = exerciseId,
    weight = weight,
    repetitions = repetitions,
    completed = completed,
    order = order,
)
