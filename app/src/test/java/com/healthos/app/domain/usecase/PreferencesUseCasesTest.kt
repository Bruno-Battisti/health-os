package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.PersonalGoal
import com.healthos.app.domain.repository.PreferencesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PreferencesUseCasesTest {

    private val repository = mockk<PreferencesRepository>()

    @Test
    fun `SetPersonalGoalUseCase delegates to repository`() = runTest {
        coEvery { repository.setPersonalGoal(PersonalGoal.LOSE_WEIGHT) } returns Unit

        SetPersonalGoalUseCase(repository)(PersonalGoal.LOSE_WEIGHT)

        coVerify(exactly = 1) { repository.setPersonalGoal(PersonalGoal.LOSE_WEIGHT) }
    }

    @Test
    fun `GetPersonalGoalUseCase returns the repository flow`() = runTest {
        every { repository.observePersonalGoal() } returns flowOf(PersonalGoal.GAIN_MUSCLE)

        val result = GetPersonalGoalUseCase(repository)().first()

        assertEquals(PersonalGoal.GAIN_MUSCLE, result)
    }

    @Test
    fun `CompleteOnboardingUseCase marks onboarding as completed`() = runTest {
        coEvery { repository.setOnboardingCompleted(true) } returns Unit

        CompleteOnboardingUseCase(repository)()

        coVerify(exactly = 1) { repository.setOnboardingCompleted(true) }
    }

    @Test
    fun `ObserveOnboardingCompletedUseCase returns the repository flow`() = runTest {
        every { repository.observeOnboardingCompleted() } returns flowOf(true)

        assertTrue(ObserveOnboardingCompletedUseCase(repository)().first())
    }
}
