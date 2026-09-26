@file:OptIn(ExperimentalCoroutinesApi::class)

package com.healthos.app.presentation.goal

import com.healthos.app.domain.model.User
import com.healthos.app.domain.usecase.AddGoalUseCase
import com.healthos.app.domain.usecase.CheckInGoalUseCase
import com.healthos.app.domain.usecase.DeleteGoalUseCase
import com.healthos.app.domain.usecase.GetGoalsUseCase
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
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GoalsViewModelTest {

    private val addGoalUseCase = mockk<AddGoalUseCase>()
    private val checkInGoalUseCase = mockk<CheckInGoalUseCase>()
    private val deleteGoalUseCase = mockk<DeleteGoalUseCase>()
    private val getGoalsUseCase = mockk<GetGoalsUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { getGoalsUseCase(User.SINGLE_USER_ID) } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = GoalsViewModel(addGoalUseCase, checkInGoalUseCase, deleteGoalUseCase, getGoalsUseCase)

    @Test
    fun `confirmAddGoal does nothing when the deadline is invalid`() = runTest {
        val viewModel = buildViewModel()

        viewModel.onDescriptionChange("Treinar 4x")
        viewModel.onTargetCountChange("4")
        viewModel.onDeadlineChange("not-a-date")
        viewModel.confirmAddGoal()

        coVerify(exactly = 0) { addGoalUseCase(any(), any(), any(), any()) }
    }

    @Test
    fun `confirmAddGoal with valid data creates the goal and closes the dialog`() = runTest {
        val deadline = LocalDate.of(2026, 12, 31)
        coEvery { addGoalUseCase(User.SINGLE_USER_ID, "Treinar 4x", 4, deadline) } returns 1L
        val viewModel = buildViewModel()

        viewModel.openAddDialog()
        viewModel.onDescriptionChange("Treinar 4x")
        viewModel.onTargetCountChange("4")
        viewModel.onDeadlineChange("2026-12-31")
        viewModel.confirmAddGoal()

        coVerify(exactly = 1) { addGoalUseCase(User.SINGLE_USER_ID, "Treinar 4x", 4, deadline) }
        assertFalse(viewModel.uiState.value.isAddDialogOpen)
    }

    @Test
    fun `checkIn delegates to the use case`() = runTest {
        coEvery { checkInGoalUseCase(1L, any()) } returns Unit

        buildViewModel().checkIn(1L)

        coVerify(exactly = 1) { checkInGoalUseCase(1L, any()) }
    }

    @Test
    fun `deleteGoal delegates to the use case`() = runTest {
        coEvery { deleteGoalUseCase(1L) } returns Unit

        buildViewModel().deleteGoal(1L)

        coVerify(exactly = 1) { deleteGoalUseCase(1L) }
    }
}
