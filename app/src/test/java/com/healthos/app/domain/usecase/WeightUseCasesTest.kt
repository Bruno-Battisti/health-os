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

class WeightUseCasesTest {

    private val repository = mockk<WeightRepository>()

    @Test
    fun `RecordInitialWeightUseCase stores an entry for the single user`() = runTest {
        val date = LocalDate.of(2026, 1, 1)
        val expected = WeightEntry(userId = User.SINGLE_USER_ID, weight = 80f, date = date)
        coEvery { repository.addEntry(expected) } returns Unit

        RecordInitialWeightUseCase(repository)(80f, date)

        coVerify(exactly = 1) { repository.addEntry(expected) }
    }

    @Test
    fun `GetLatestWeightUseCase returns the repository flow for the given user`() = runTest {
        val entry = WeightEntry(userId = 1L, weight = 79f, date = LocalDate.of(2026, 1, 8))
        every { repository.observeLatest(1L) } returns flowOf(entry)

        val result = GetLatestWeightUseCase(repository)(1L).first()

        assertEquals(entry, result)
    }
}
