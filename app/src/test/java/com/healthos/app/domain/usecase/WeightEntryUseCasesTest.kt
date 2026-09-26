package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.User
import com.healthos.app.domain.model.WeightEntry
import com.healthos.app.domain.repository.WeightRepository
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

class WeightEntryUseCasesTest {

    private val repository = mockk<WeightRepository>()

    @Test
    fun `AddWeightEntryUseCase stores an entry for the single user`() = runTest {
        val date = LocalDate.of(2026, 1, 1)
        val expected = WeightEntry(userId = User.SINGLE_USER_ID, weight = 80f, date = date, note = "manhã")
        coEvery { repository.addEntry(expected) } returns Unit

        AddWeightEntryUseCase(repository)(80f, date, "manhã")

        coVerify(exactly = 1) { repository.addEntry(expected) }
    }

    @Test
    fun `UpdateWeightEntryUseCase delegates to repository`() = runTest {
        val entry = WeightEntry(id = 1, userId = User.SINGLE_USER_ID, weight = 78f, date = LocalDate.of(2026, 1, 1))
        coEvery { repository.updateEntry(entry) } returns Unit

        UpdateWeightEntryUseCase(repository)(entry)

        coVerify(exactly = 1) { repository.updateEntry(entry) }
    }

    @Test
    fun `DeleteWeightEntryUseCase delegates to repository`() = runTest {
        coEvery { repository.deleteEntry(1L) } returns Unit

        DeleteWeightEntryUseCase(repository)(1L)

        coVerify(exactly = 1) { repository.deleteEntry(1L) }
    }

    @Test
    fun `GetWeightHistoryUseCase returns the repository flow`() = runTest {
        val entries = listOf(WeightEntry(userId = User.SINGLE_USER_ID, weight = 80f, date = LocalDate.of(2026, 1, 1)))
        every { repository.observeHistory(User.SINGLE_USER_ID) } returns flowOf(entries)

        assertEquals(entries, GetWeightHistoryUseCase(repository)(User.SINGLE_USER_ID).first())
    }
}
