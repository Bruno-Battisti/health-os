@file:OptIn(ExperimentalCoroutinesApi::class)

package com.healthos.app.presentation.habit

import com.healthos.app.domain.model.Habit
import com.healthos.app.domain.model.User
import com.healthos.app.domain.usecase.AddHabitUseCase
import com.healthos.app.domain.usecase.DeleteHabitUseCase
import com.healthos.app.domain.usecase.GetHabitProgressUseCase
import com.healthos.app.domain.usecase.GetHabitsUseCase
import com.healthos.app.domain.usecase.LogHabitEntryUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class HabitsViewModelTest {

    private val addHabitUseCase = mockk<AddHabitUseCase>()
    private val deleteHabitUseCase = mockk<DeleteHabitUseCase>()
    private val logHabitEntryUseCase = mockk<LogHabitEntryUseCase>()
    private val getHabitsUseCase = mockk<GetHabitsUseCase>()
    private val getHabitProgressUseCase = mockk<GetHabitProgressUseCase>()
    private val habit = Habit(id = 1, userId = User.SINGLE_USER_ID, name = "Água", targetValue = 2.5f, unit = "L")

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { getHabitsUseCase(User.SINGLE_USER_ID) } returns flowOf(listOf(habit))
        every { getHabitProgressUseCase(1L, any()) } returns flowOf(0.5f)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() =
        HabitsViewModel(addHabitUseCase, deleteHabitUseCase, logHabitEntryUseCase, getHabitsUseCase, getHabitProgressUseCase)

    @Test
    fun `exposes each habit with its progress for today`() = runTest {
        val viewModel = buildViewModel()

        assertEquals(1, viewModel.uiState.value.habits.size)
        assertEquals(0.5f, viewModel.uiState.value.habits.first().todayValue)
    }

    @Test
    fun `logEntry with a valid value delegates to the use case and clears the input`() = runTest {
        coEvery { logHabitEntryUseCase(1L, 0.3f, any()) } returns Unit
        val viewModel = buildViewModel()

        viewModel.onLogInputChange(1L, "0.3")
        viewModel.logEntry(1L)

        coVerify(exactly = 1) { logHabitEntryUseCase(1L, 0.3f, any()) }
        assertEquals("", viewModel.uiState.value.logInputs[1L])
    }

    @Test
    fun `confirmAddHabit does nothing when the target is invalid`() = runTest {
        val viewModel = buildViewModel()

        viewModel.onNewHabitNameChange("Passos")
        viewModel.onNewHabitTargetChange("abc")
        viewModel.confirmAddHabit()

        coVerify(exactly = 0) { addHabitUseCase(any(), any(), any(), any()) }
    }

    @Test
    fun `confirmAddHabit with valid data creates the habit and closes the dialog`() = runTest {
        coEvery { addHabitUseCase(User.SINGLE_USER_ID, "Passos", 8000f, "passos") } returns 2L
        val viewModel = buildViewModel()

        viewModel.openAddDialog()
        viewModel.onNewHabitNameChange("Passos")
        viewModel.onNewHabitTargetChange("8000")
        viewModel.onNewHabitUnitChange("passos")
        viewModel.confirmAddHabit()

        coVerify(exactly = 1) { addHabitUseCase(User.SINGLE_USER_ID, "Passos", 8000f, "passos") }
        assertFalse(viewModel.uiState.value.isAddDialogOpen)
    }

    @Test
    fun `deleteHabit delegates to the use case`() = runTest {
        coEvery { deleteHabitUseCase(1L) } returns Unit

        buildViewModel().deleteHabit(1L)

        coVerify(exactly = 1) { deleteHabitUseCase(1L) }
    }
}
