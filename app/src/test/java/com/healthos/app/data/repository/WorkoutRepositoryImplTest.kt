package com.healthos.app.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.healthos.app.data.local.AppDatabase
import com.healthos.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class WorkoutRepositoryImplTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: WorkoutRepositoryImpl
    private val userId = 1L

    @Before
    fun setUp() = runTest {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        database.userDao().upsert(UserEntity(id = userId, name = "Ana", birthDate = 0, height = 165f, createdAt = 0))
        repository = WorkoutRepositoryImpl(database.workoutDao(), database.exerciseDao(), database.exerciseSetDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `a newly created workout has no exercises`() = runTest {
        val workoutId = repository.createWorkout(userId, "Treino A", LocalDate.of(2026, 1, 1))

        val workout = repository.observeWorkout(workoutId).first()

        assertEquals("Treino A", workout?.name)
        assertTrue(workout?.exercises.orEmpty().isEmpty())
    }

    @Test
    fun `adding an exercise and a set nests correctly under the workout`() = runTest {
        val workoutId = repository.createWorkout(userId, "Treino A", LocalDate.of(2026, 1, 1))
        val exerciseId = repository.addExercise(workoutId, "Supino", 0)
        repository.addSet(exerciseId, weight = 80f, repetitions = 10, order = 0)

        val workout = repository.observeWorkout(workoutId).first()

        assertEquals(1, workout?.exercises?.size)
        assertEquals("Supino", workout?.exercises?.first()?.name)
        assertEquals(1, workout?.exercises?.first()?.sets?.size)
        assertEquals(80f, workout?.exercises?.first()?.sets?.first()?.weight)
    }

    @Test
    fun `updateSet toggles the completed flag`() = runTest {
        val workoutId = repository.createWorkout(userId, "Treino A", LocalDate.of(2026, 1, 1))
        val exerciseId = repository.addExercise(workoutId, "Supino", 0)
        repository.addSet(exerciseId, weight = 80f, repetitions = 10, order = 0)
        val set = repository.observeWorkout(workoutId).first()!!.exercises.first().sets.first()

        repository.updateSet(set.copy(completed = true))

        val updated = repository.observeWorkout(workoutId).first()!!.exercises.first().sets.first()
        assertTrue(updated.completed)
    }

    @Test
    fun `finishWorkout stores the duration`() = runTest {
        val workoutId = repository.createWorkout(userId, "Treino A", LocalDate.of(2026, 1, 1))

        repository.finishWorkout(workoutId, 45)

        assertEquals(45, repository.observeHistory(userId).first().first().durationMinutes)
    }

    @Test
    fun `deleteWorkout cascades and removes it from the history`() = runTest {
        val workoutId = repository.createWorkout(userId, "Treino A", LocalDate.of(2026, 1, 1))
        repository.addExercise(workoutId, "Supino", 0)

        repository.deleteWorkout(workoutId)

        assertTrue(repository.observeHistory(userId).first().isEmpty())
        assertNull(repository.observeWorkout(workoutId).first())
    }
}
