@file:OptIn(ExperimentalCoroutinesApi::class)

package com.healthos.app.presentation.workout

import com.healthos.app.domain.model.User
import com.healthos.app.domain.usecase.CreateWorkoutUseCase
import com.healthos.app.domain.usecase.DeleteWorkoutUseCase
import com.healthos.app.domain.usecase.GetWorkoutHistoryUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class WorkoutListViewModelTest {

    private val createWorkoutUseCase = mockk<CreateWorkoutUseCase>()
    private val deleteWorkoutUseCase = mockk<DeleteWorkoutUseCase>()
    private val getWorkoutHistoryUseCase = mockk<GetWorkoutHistoryUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { getWorkoutHistoryUseCase(User.SINGLE_USER_ID) } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = WorkoutListViewModel(createWorkoutUseCase, deleteWorkoutUseCase, getWorkoutHistoryUseCase)

    @Test
    fun `confirmCreateWorkout creates the workout, closes the dialog and emits the new id`() = runTest {
        coEvery { createWorkoutUseCase(User.SINGLE_USER_ID, "Treino A", any()) } returns 10L
        val viewModel = buildViewModel()
        var emittedId: Long? = null
        val collectJob = launch { emittedId = viewModel.createdWorkoutId.first() }

        viewModel.openCreateDialog()
        viewModel.onNewWorkoutNameChange("Treino A")
        viewModel.confirmCreateWorkout()
        collectJob.join()

        assertEquals(10L, emittedId)
        assertFalse(viewModel.uiState.value.isCreateDialogOpen)
        coVerify(exactly = 1) { createWorkoutUseCase(User.SINGLE_USER_ID, "Treino A", any()) }
    }

    @Test
    fun `deleteWorkout delegates to the use case`() = runTest {
        coEvery { deleteWorkoutUseCase(1L) } returns Unit

        buildViewModel().deleteWorkout(1L)

        coVerify(exactly = 1) { deleteWorkoutUseCase(1L) }
    }
}
