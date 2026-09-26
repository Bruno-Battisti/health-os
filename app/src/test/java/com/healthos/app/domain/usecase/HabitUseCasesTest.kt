package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.Habit
import com.healthos.app.domain.repository.HabitRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class HabitUseCasesTest {

    private val repository = mockk<HabitRepository>()

    @Test
    fun `AddHabitUseCase delegates to repository`() = runTest {
        coEvery { repository.addHabit(1L, "Água", 2.5f, "L") } returns 10L

        assertEquals(10L, AddHabitUseCase(repository)(1L, "Água", 2.5f, "L"))
    }

    @Test
    fun `LogHabitEntryUseCase delegates to repository`() = runTest {
        val date = LocalDate.of(2026, 1, 1)
        coEvery { repository.logEntry(1L, 0.5f, date) } returns Unit

        LogHabitEntryUseCase(repository)(1L, 0.5f, date)

        coVerify(exactly = 1) { repository.logEntry(1L, 0.5f, date) }
    }

    @Test
    fun `GetHabitsUseCase returns the repository flow`() = runTest {
        val habits = listOf(Habit(userId = 1L, name = "Água", targetValue = 2.5f, unit = "L"))
        every { repository.observeHabits(1L) } returns flowOf(habits)

        assertEquals(habits, GetHabitsUseCase(repository)(1L).first())
    }

    @Test
    fun `GetHabitProgressUseCase sums the entries for that date`() = runTest {
        val date = LocalDate.of(2026, 1, 1)
        every { repository.observeEntriesForDate(1L, date) } returns flowOf(
            listOf(
                com.healthos.app.domain.model.HabitEntry(habitId = 1L, value = 0.5f, date = date),
                com.healthos.app.domain.model.HabitEntry(habitId = 1L, value = 0.3f, date = date),
            ),
        )

        assertEquals(0.8f, GetHabitProgressUseCase(repository)(1L, date).first())
    }

    @Test
    fun `SeedDefaultHabitsUseCase delegates to repository`() = runTest {
        coEvery { repository.seedDefaultHabits(1L) } returns Unit

        SeedDefaultHabitsUseCase(repository)(1L)

        coVerify(exactly = 1) { repository.seedDefaultHabits(1L) }
    }

    @Test
    fun `DeleteHabitUseCase delegates to repository`() = runTest {
        coEvery { repository.deleteHabit(1L) } returns Unit

        DeleteHabitUseCase(repository)(1L)

        coVerify(exactly = 1) { repository.deleteHabit(1L) }
    }
}
