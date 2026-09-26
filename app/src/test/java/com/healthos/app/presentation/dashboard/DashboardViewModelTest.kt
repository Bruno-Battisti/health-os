@file:OptIn(ExperimentalCoroutinesApi::class)

package com.healthos.app.presentation.dashboard

import com.healthos.app.domain.model.User
import com.healthos.app.domain.model.WeightEntry
import com.healthos.app.domain.usecase.GetHabitProgressUseCase
import com.healthos.app.domain.usecase.GetHabitsUseCase
import com.healthos.app.domain.usecase.GetHealthConnectStatusUseCase
import com.healthos.app.domain.usecase.GetLatestWeightUseCase
import com.healthos.app.domain.usecase.GetTodayStepsUseCase
import com.healthos.app.domain.usecase.GetUserProfileUseCase
import com.healthos.app.domain.usecase.GetWeightTrendUseCase
import com.healthos.app.domain.usecase.HealthConnectStatus
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.Period

class DashboardViewModelTest {

    private val userFlow = MutableStateFlow<User?>(null)
    private val weightFlow = MutableStateFlow<WeightEntry?>(null)
    private val getUserProfileUseCase = mockk<GetUserProfileUseCase>()
    private val getLatestWeightUseCase = mockk<GetLatestWeightUseCase>()
    private val getWeightTrendUseCase = mockk<GetWeightTrendUseCase>()
    private val getHabitsUseCase = mockk<GetHabitsUseCase>()
    private val getHabitProgressUseCase = mockk<GetHabitProgressUseCase>()
    private val getHealthConnectStatusUseCase = mockk<GetHealthConnectStatusUseCase>()
    private val getTodayStepsUseCase = mockk<GetTodayStepsUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { getUserProfileUseCase() } returns userFlow
        every { getLatestWeightUseCase(User.SINGLE_USER_ID) } returns weightFlow
        every { getWeightTrendUseCase(User.SINGLE_USER_ID, any()) } returns MutableStateFlow(null)
        every { getHabitsUseCase(User.SINGLE_USER_ID) } returns flowOf(emptyList())
        coEvery { getHealthConnectStatusUseCase() } returns HealthConnectStatus(
            isAvailable = false,
            hasAllPermissions = false,
            requiredPermissions = emptySet(),
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = DashboardViewModel(
        getUserProfileUseCase, getLatestWeightUseCase, getWeightTrendUseCase, getHabitsUseCase, getHabitProgressUseCase,
        getHealthConnectStatusUseCase, getTodayStepsUseCase,
    )

    @Test
    fun `dashboard reflects the latest weight after registering 79kg over an initial 80kg`() = runTest {
        val birthDate = LocalDate.now().minusYears(30)
        val viewModel = buildViewModel()

        userFlow.value = User(name = "Ana", birthDate = birthDate, heightCm = 165f, createdAt = Instant.now())
        weightFlow.value = WeightEntry(userId = User.SINGLE_USER_ID, weight = 80f, date = LocalDate.now())

        assertEquals(80f, viewModel.uiState.value.currentWeight)
        assertEquals("Ana", viewModel.uiState.value.userName)
        assertEquals(30, viewModel.uiState.value.userAge)

        weightFlow.value = WeightEntry(userId = User.SINGLE_USER_ID, weight = 79f, date = LocalDate.now())

        assertEquals(79f, viewModel.uiState.value.currentWeight)
    }
}
