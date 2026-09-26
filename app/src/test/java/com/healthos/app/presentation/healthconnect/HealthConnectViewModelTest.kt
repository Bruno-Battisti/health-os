@file:OptIn(ExperimentalCoroutinesApi::class)

package com.healthos.app.presentation.healthconnect

import com.healthos.app.domain.usecase.GetHealthConnectStatusUseCase
import com.healthos.app.domain.usecase.GetLastNightSleepUseCase
import com.healthos.app.domain.usecase.GetTodayStepsUseCase
import com.healthos.app.domain.usecase.GetWeeklyExerciseSessionCountUseCase
import com.healthos.app.domain.usecase.HealthConnectStatus
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class HealthConnectViewModelTest {

    private val getHealthConnectStatusUseCase = mockk<GetHealthConnectStatusUseCase>()
    private val getTodayStepsUseCase = mockk<GetTodayStepsUseCase>()
    private val getLastNightSleepUseCase = mockk<GetLastNightSleepUseCase>()
    private val getWeeklyExerciseSessionCountUseCase = mockk<GetWeeklyExerciseSessionCountUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = HealthConnectViewModel(
        getHealthConnectStatusUseCase, getTodayStepsUseCase, getLastNightSleepUseCase, getWeeklyExerciseSessionCountUseCase,
    )

    @Test
    fun `shows the permission request when permissions are missing`() = runTest {
        val permissions = setOf("android.permission.health.READ_STEPS")
        coEvery { getHealthConnectStatusUseCase() } returns HealthConnectStatus(
            isAvailable = true,
            hasAllPermissions = false,
            requiredPermissions = permissions,
        )

        val viewModel = buildViewModel()

        assertFalse(viewModel.uiState.value.hasPermissions)
        assertEquals(permissions, viewModel.uiState.value.requiredPermissions)
    }

    @Test
    fun `loads steps, sleep and exercise data once permissions are granted`() = runTest {
        coEvery { getHealthConnectStatusUseCase() } returns HealthConnectStatus(
            isAvailable = true,
            hasAllPermissions = true,
            requiredPermissions = emptySet(),
        )
        coEvery { getTodayStepsUseCase() } returns 4200L
        coEvery { getLastNightSleepUseCase() } returns 420L
        coEvery { getWeeklyExerciseSessionCountUseCase() } returns 3

        val viewModel = buildViewModel()

        assertEquals(4200L, viewModel.uiState.value.steps)
        assertEquals(420L, viewModel.uiState.value.sleepMinutes)
        assertEquals(3, viewModel.uiState.value.exerciseSessionsThisWeek)
    }
}
