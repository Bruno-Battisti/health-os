@file:OptIn(ExperimentalCoroutinesApi::class)

package com.healthos.app.presentation.profile

import com.healthos.app.domain.model.PersonalGoal
import com.healthos.app.domain.model.User
import com.healthos.app.domain.usecase.GetPersonalGoalUseCase
import com.healthos.app.domain.usecase.GetUserProfileUseCase
import com.healthos.app.domain.usecase.SaveUserProfileUseCase
import com.healthos.app.domain.usecase.SetPersonalGoalUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class ProfileViewModelTest {

    private val getUserProfileUseCase = mockk<GetUserProfileUseCase>()
    private val getPersonalGoalUseCase = mockk<GetPersonalGoalUseCase>()
    private val saveUserProfileUseCase = mockk<SaveUserProfileUseCase>()
    private val setPersonalGoalUseCase = mockk<SetPersonalGoalUseCase>()
    private val createdAt = Instant.ofEpochSecond(1_700_000_000)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { getUserProfileUseCase() } returns flowOf(
            User(name = "Ana", birthDate = LocalDate.of(1995, 4, 10), heightCm = 165f, createdAt = createdAt),
        )
        every { getPersonalGoalUseCase() } returns flowOf(PersonalGoal.MAINTAIN_WEIGHT)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = ProfileViewModel(
        getUserProfileUseCase,
        getPersonalGoalUseCase,
        saveUserProfileUseCase,
        setPersonalGoalUseCase,
    )

    @Test
    fun `loads existing profile into editable fields`() = runTest {
        val viewModel = buildViewModel()

        assertEquals("Ana", viewModel.uiState.value.name)
        assertEquals("1995-04-10", viewModel.uiState.value.birthDate)
        assertEquals("165.0", viewModel.uiState.value.heightCm)
        assertEquals(PersonalGoal.MAINTAIN_WEIGHT, viewModel.uiState.value.goal)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `save keeps the original createdAt and preserves the edited fields`() = runTest {
        val savedUser = slot<User>()
        coEvery { saveUserProfileUseCase(capture(savedUser)) } returns Unit
        coEvery { setPersonalGoalUseCase(PersonalGoal.MAINTAIN_WEIGHT) } returns Unit

        val viewModel = buildViewModel()
        viewModel.onNameChange("Ana Paula")
        viewModel.onHeightChange("166")

        viewModel.save()

        assertTrue(viewModel.uiState.value.saveSuccess)
        // updatedAt is stamped with the current time on save, so it can't be known ahead of the
        // call; every other field is asserted exactly.
        assertEquals(
            User(name = "Ana Paula", birthDate = LocalDate.of(1995, 4, 10), heightCm = 166f, createdAt = createdAt, updatedAt = savedUser.captured.updatedAt),
            savedUser.captured,
        )
    }
}
