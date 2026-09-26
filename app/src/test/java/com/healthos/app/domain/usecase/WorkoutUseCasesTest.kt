package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.ExerciseSet
import com.healthos.app.domain.model.Workout
import com.healthos.app.domain.repository.WorkoutRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class WorkoutUseCasesTest {

    private val repository = mockk<WorkoutRepository>()

    @Test
    fun `CreateWorkoutUseCase delegates to repository`() = runTest {
        val date = LocalDate.of(2026, 1, 1)
        coEvery { repository.createWorkout(1L, "Treino A", date) } returns 10L

        val id = CreateWorkoutUseCase(repository)(1L, "Treino A", date)

        assertEquals(10L, id)
        coVerify(exactly = 1) { repository.createWorkout(1L, "Treino A", date) }
    }

    @Test
    fun `AddExerciseUseCase delegates to repository`() = runTest {
        coEvery { repository.addExercise(1L, "Supino", 0) } returns 5L

        assertEquals(5L, AddExerciseUseCase(repository)(1L, "Supino", 0))
    }

    @Test
    fun `AddSetUseCase delegates to repository`() = runTest {
        coEvery { repository.addSet(5L, 80f, 10, 0) } returns 7L

        assertEquals(7L, AddSetUseCase(repository)(5L, 80f, 10, 0))
    }

    @Test
    fun `ToggleSetCompletedUseCase flips the completed flag`() = runTest {
        val set = ExerciseSet(id = 1, exerciseId = 5, weight = 80f, repetitions = 10, completed = false, order = 0)
        coEvery { repository.updateSet(set.copy(completed = true)) } returns Unit

        ToggleSetCompletedUseCase(repository)(set)

        coVerify(exactly = 1) { repository.updateSet(set.copy(completed = true)) }
    }

    @Test
    fun `FinishWorkoutUseCase delegates to repository`() = runTest {
        coEvery { repository.finishWorkout(1L, 45) } returns Unit

        FinishWorkoutUseCase(repository)(1L, 45)

        coVerify(exactly = 1) { repository.finishWorkout(1L, 45) }
    }

    @Test
    fun `DeleteWorkoutUseCase delegates to repository`() = runTest {
        coEvery { repository.deleteWorkout(1L) } returns Unit

        DeleteWorkoutUseCase(repository)(1L)

        coVerify(exactly = 1) { repository.deleteWorkout(1L) }
    }

    @Test
    fun `GetWorkoutHistoryUseCase returns the repository flow`() = runTest {
        val history = listOf(Workout(userId = 1L, name = "Treino A", date = LocalDate.of(2026, 1, 1)))
        every { repository.observeHistory(1L) } returns flowOf(history)

        assertEquals(history, GetWorkoutHistoryUseCase(repository)(1L).first())
    }

    @Test
    fun `GetWorkoutUseCase returns the repository flow for a single workout`() = runTest {
        val workout = Workout(id = 1, userId = 1L, name = "Treino A", date = LocalDate.of(2026, 1, 1))
        every { repository.observeWorkout(1L) } returns flowOf(workout)

        assertEquals(workout, GetWorkoutUseCase(repository)(1L).first())
    }
}
