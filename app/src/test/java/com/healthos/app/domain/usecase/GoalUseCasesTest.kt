package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.Goal
import com.healthos.app.domain.model.GoalWithProgress
import com.healthos.app.domain.repository.GoalRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class GoalUseCasesTest {

    private val repository = mockk<GoalRepository>()

    @Test
    fun `AddGoalUseCase delegates to repository`() = runTest {
        val deadline = LocalDate.of(2026, 12, 31)
        coEvery { repository.addGoal(1L, "Treinar 4x", 4, deadline) } returns 10L

        assertEquals(10L, AddGoalUseCase(repository)(1L, "Treinar 4x", 4, deadline))
    }

    @Test
    fun `CheckInGoalUseCase delegates to repository`() = runTest {
        val date = LocalDate.of(2026, 1, 1)
        coEvery { repository.checkIn(1L, date) } returns Unit

        CheckInGoalUseCase(repository)(1L, date)

        coVerify(exactly = 1) { repository.checkIn(1L, date) }
    }

    @Test
    fun `DeleteGoalUseCase delegates to repository`() = runTest {
        coEvery { repository.deleteGoal(1L) } returns Unit

        DeleteGoalUseCase(repository)(1L)

        coVerify(exactly = 1) { repository.deleteGoal(1L) }
    }

    @Test
    fun `GetGoalsUseCase returns the repository flow`() = runTest {
        val goals = listOf(
            GoalWithProgress(
                Goal(userId = 1L, description = "Treinar 4x", targetCount = 4, deadline = LocalDate.of(2026, 12, 31), createdAt = Instant.now()),
                checkInCount = 2,
            ),
        )
        every { repository.observeGoals(1L) } returns flowOf(goals)

        assertEquals(goals, GetGoalsUseCase(repository)(1L).first())
    }
}
