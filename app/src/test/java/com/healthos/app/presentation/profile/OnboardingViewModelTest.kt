@file:OptIn(ExperimentalCoroutinesApi::class)

package com.healthos.app.presentation.profile

import com.healthos.app.domain.model.PersonalGoal
import com.healthos.app.domain.usecase.CompleteOnboardingUseCase
import com.healthos.app.domain.usecase.RecordInitialWeightUseCase
import com.healthos.app.domain.usecase.SaveUserProfileUseCase
import com.healthos.app.domain.usecase.SetPersonalGoalUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OnboardingViewModelTest {

    private val saveUserProfileUseCase = mockk<SaveUserProfileUseCase>()
    private val setPersonalGoalUseCase = mockk<SetPersonalGoalUseCase>()
    private val recordInitialWeightUseCase = mockk<RecordInitialWeightUseCase>()
    private val completeOnboardingUseCase = mockk<CompleteOnboardingUseCase>()

    private val viewModel = OnboardingViewModel(
        saveUserProfileUseCase,
        setPersonalGoalUseCase,
        recordInitialWeightUseCase,
        completeOnboardingUseCase,
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `submit with blank fields reports an error and does not save`() = runTest {
        viewModel.submit()

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isComplete)
        coVerify(exactly = 0) { saveUserProfileUseCase(any()) }
    }

    @Test
    fun `submit with valid fields saves profile, weight, goal and completes onboarding`() = runTest {
        coEvery { saveUserProfileUseCase(any()) } returns Unit
        coEvery { recordInitialWeightUseCase(any(), any()) } returns Unit
        coEvery { setPersonalGoalUseCase(any()) } returns Unit
        coEvery { completeOnboardingUseCase() } returns Unit

        viewModel.onNameChange("Ana")
        viewModel.onBirthDateChange("1995-04-10")
        viewModel.onHeightChange("165")
        viewModel.onInitialWeightChange("60")
        viewModel.onGoalChange(PersonalGoal.MAINTAIN_WEIGHT)

        viewModel.submit()

        assertTrue(viewModel.uiState.value.isComplete)
        assertEquals(null, viewModel.uiState.value.errorMessage)
        coVerify(exactly = 1) { saveUserProfileUseCase(any()) }
        coVerify(exactly = 1) { recordInitialWeightUseCase(60f, any()) }
        coVerify(exactly = 1) { setPersonalGoalUseCase(PersonalGoal.MAINTAIN_WEIGHT) }
        coVerify(exactly = 1) { completeOnboardingUseCase() }
    }

    @Test
    fun `submit with invalid birth date reports an error`() = runTest {
        viewModel.onNameChange("Ana")
        viewModel.onBirthDateChange("not-a-date")
        viewModel.onHeightChange("165")
        viewModel.onInitialWeightChange("60")
        viewModel.onGoalChange(PersonalGoal.MAINTAIN_WEIGHT)

        viewModel.submit()

        assertNotNull(viewModel.uiState.value.errorMessage)
        coVerify(exactly = 0) { saveUserProfileUseCase(any()) }
    }
}
