@file:OptIn(ExperimentalCoroutinesApi::class)

package com.healthos.app.presentation.workout

import androidx.lifecycle.SavedStateHandle
import com.healthos.app.domain.model.Exercise
import com.healthos.app.domain.model.ExerciseSet
import com.healthos.app.domain.model.Workout
import com.healthos.app.domain.usecase.AddExerciseUseCase
import com.healthos.app.domain.usecase.AddSetUseCase
import com.healthos.app.domain.usecase.FinishWorkoutUseCase
import com.healthos.app.domain.usecase.GetWorkoutUseCase
import com.healthos.app.domain.usecase.ToggleSetCompletedUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class ActiveWorkoutViewModelTest {

    private val workoutFlow = MutableStateFlow<Workout?>(null)
    private val addExerciseUseCase = mockk<AddExerciseUseCase>()
    private val addSetUseCase = mockk<AddSetUseCase>()
    private val toggleSetCompletedUseCase = mockk<ToggleSetCompletedUseCase>()
    private val finishWorkoutUseCase = mockk<FinishWorkoutUseCase>()
    private val getWorkoutUseCase = mockk<GetWorkoutUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { getWorkoutUseCase(1L) } returns workoutFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = ActiveWorkoutViewModel(
        SavedStateHandle(mapOf("workoutId" to 1L)),
        addExerciseUseCase,
        addSetUseCase,
        toggleSetCompletedUseCase,
        finishWorkoutUseCase,
        getWorkoutUseCase,
    )

    @Test
    fun `exposes the workout emitted by the use case`() = runTest {
        val workout = Workout(id = 1, userId = 1L, name = "Treino A", date = LocalDate.of(2026, 1, 1))
        workoutFlow.value = workout

        val viewModel = buildViewModel()

        assertEquals(workout, viewModel.uiState.value.workout)
    }

    @Test
    fun `addExercise uses the current exercise count as the order`() = runTest {
        val workout = Workout(
            id = 1, userId = 1L, name = "Treino A", date = LocalDate.of(2026, 1, 1),
            exercises = listOf(Exercise(id = 1, workoutId = 1, name = "Supino", order = 0)),
        )
        workoutFlow.value = workout
        coEvery { addExerciseUseCase(1L, "Agachamento", 1) } returns 2L

        val viewModel = buildViewModel()
        viewModel.onNewExerciseNameChange("Agachamento")
        viewModel.addExercise()

        coVerify(exactly = 1) { addExerciseUseCase(1L, "Agachamento", 1) }
        assertEquals("", viewModel.uiState.value.newExerciseName)
    }

    @Test
    fun `addSet does nothing when weight or reps are invalid`() = runTest {
        val viewModel = buildViewModel()

        viewModel.onSetWeightChange(5L, "abc")
        viewModel.onSetRepsChange(5L, "10")
        viewModel.addSet(5L)

        coVerify(exactly = 0) { addSetUseCase(any(), any(), any(), any()) }
    }

    @Test
    fun `addSet delegates with the current set count as order`() = runTest {
        val exercise = Exercise(
            id = 5, workoutId = 1, name = "Supino", order = 0,
            sets = listOf(ExerciseSet(id = 1, exerciseId = 5, weight = 80f, repetitions = 10, completed = true, order = 0)),
        )
        workoutFlow.value = Workout(id = 1, userId = 1L, name = "Treino A", date = LocalDate.of(2026, 1, 1), exercises = listOf(exercise))
        coEvery { addSetUseCase(5L, 82.5f, 8, 1) } returns 2L

        val viewModel = buildViewModel()
        viewModel.onSetWeightChange(5L, "82.5")
        viewModel.onSetRepsChange(5L, "8")
        viewModel.addSet(5L)

        coVerify(exactly = 1) { addSetUseCase(5L, 82.5f, 8, 1) }
    }

    @Test
    fun `toggleSetCompleted delegates to the use case`() = runTest {
        val set = ExerciseSet(id = 1, exerciseId = 5, weight = 80f, repetitions = 10, completed = false, order = 0)
        coEvery { toggleSetCompletedUseCase(set) } returns Unit

        buildViewModel().toggleSetCompleted(set)

        coVerify(exactly = 1) { toggleSetCompletedUseCase(set) }
    }

    @Test
    fun `finishWorkout calls the use case and marks the state as finished`() = runTest {
        coEvery { finishWorkoutUseCase(1L, any()) } returns Unit

        val viewModel = buildViewModel()
        viewModel.finishWorkout()

        coVerify(exactly = 1) { finishWorkoutUseCase(1L, any()) }
        assertTrue(viewModel.uiState.value.isFinished)
    }

    @Test
    fun `toggleTimer starts and resetTimer restores the default duration`() = runTest {
        val viewModel = buildViewModel()

        viewModel.toggleTimer()
        assertTrue(viewModel.uiState.value.isTimerRunning)

        viewModel.resetTimer()
        assertEquals(ActiveWorkoutUiState.DEFAULT_REST_SECONDS, viewModel.uiState.value.restSecondsRemaining)
        assertEquals(false, viewModel.uiState.value.isTimerRunning)
    }
}
