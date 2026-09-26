@file:OptIn(ExperimentalCoroutinesApi::class)

package com.healthos.app.presentation.weight

import com.healthos.app.domain.model.User
import com.healthos.app.domain.model.WeightEntry
import com.healthos.app.domain.usecase.AddWeightEntryUseCase
import com.healthos.app.domain.usecase.DeleteWeightEntryUseCase
import com.healthos.app.domain.usecase.GetWeightHistoryUseCase
import com.healthos.app.domain.usecase.GetWeightTrendUseCase
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
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class WeightViewModelTest {

    private val addWeightEntryUseCase = mockk<AddWeightEntryUseCase>()
    private val deleteWeightEntryUseCase = mockk<DeleteWeightEntryUseCase>()
    private val getWeightHistoryUseCase = mockk<GetWeightHistoryUseCase>()
    private val getWeightTrendUseCase = mockk<GetWeightTrendUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { getWeightHistoryUseCase(User.SINGLE_USER_ID) } returns flowOf(emptyList())
        every { getWeightTrendUseCase(User.SINGLE_USER_ID, any()) } returns flowOf(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = WeightViewModel(
        addWeightEntryUseCase,
        deleteWeightEntryUseCase,
        getWeightHistoryUseCase,
        getWeightTrendUseCase,
    )

    @Test
    fun `submit with an invalid weight reports an error and does not save`() = runTest {
        val viewModel = buildViewModel()

        viewModel.onWeightChange("abc")
        viewModel.submit()

        assertNotNull(viewModel.uiState.value.errorMessage)
        coVerify(exactly = 0) { addWeightEntryUseCase(any(), any(), any()) }
    }

    @Test
    fun `submit with a valid weight records it and clears the form`() = runTest {
        coEvery { addWeightEntryUseCase(79f, any(), null) } returns Unit
        val viewModel = buildViewModel()

        viewModel.onWeightChange("79")
        viewModel.submit()

        coVerify(exactly = 1) { addWeightEntryUseCase(79f, any(), null) }
        assertEquals("", viewModel.uiState.value.weightInput)
    }

    @Test
    fun `dashboard scenario - registering 79kg over an initial 80kg reflects in the history flow`() = runTest {
        val history = listOf(
            WeightEntry(id = 2, userId = User.SINGLE_USER_ID, weight = 79f, date = LocalDate.of(2026, 1, 8)),
            WeightEntry(id = 1, userId = User.SINGLE_USER_ID, weight = 80f, date = LocalDate.of(2026, 1, 1)),
        )
        every { getWeightHistoryUseCase(User.SINGLE_USER_ID) } returns flowOf(history)

        val viewModel = buildViewModel()

        assertEquals(79f, viewModel.uiState.value.history.first().weight)
    }

    @Test
    fun `delete delegates to the use case`() = runTest {
        coEvery { deleteWeightEntryUseCase(1L) } returns Unit
        val viewModel = buildViewModel()

        viewModel.delete(1L)

        coVerify(exactly = 1) { deleteWeightEntryUseCase(1L) }
    }
}
