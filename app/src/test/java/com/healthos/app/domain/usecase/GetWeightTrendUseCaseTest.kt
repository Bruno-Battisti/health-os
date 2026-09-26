package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.WeightEntry
import com.healthos.app.domain.repository.WeightRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class GetWeightTrendUseCaseTest {

    private val repository = mockk<WeightRepository>()
    private val userId = 1L

    @Test
    fun `returns null when there is no history`() = runTest {
        every { repository.observeHistory(userId) } returns flowOf(emptyList())

        assertNull(GetWeightTrendUseCase(repository)(userId, 7).first())
    }

    @Test
    fun `has a null delta when there is no entry before the period window`() = runTest {
        every { repository.observeHistory(userId) } returns flowOf(
            listOf(WeightEntry(userId = userId, weight = 80f, date = LocalDate.of(2026, 1, 1))),
        )

        val trend = GetWeightTrendUseCase(repository)(userId, 7).first()

        assertEquals(80f, trend?.currentWeight)
        assertNull(trend?.referenceWeight)
        assertNull(trend?.deltaKg)
    }

    @Test
    fun `computes a negative delta when weight went from 80kg to 79kg within the period`() = runTest {
        every { repository.observeHistory(userId) } returns flowOf(
            listOf(
                WeightEntry(userId = userId, weight = 80f, date = LocalDate.of(2026, 1, 1)),
                WeightEntry(userId = userId, weight = 79f, date = LocalDate.of(2026, 1, 8)),
            ),
        )

        val trend = GetWeightTrendUseCase(repository)(userId, 7).first()

        assertEquals(79f, trend?.currentWeight)
        assertEquals(80f, trend?.referenceWeight)
        assertEquals(-1f, trend?.deltaKg)
    }

    @Test
    fun `uses the closest entry at or before the cutoff date as the reference`() = runTest {
        every { repository.observeHistory(userId) } returns flowOf(
            listOf(
                WeightEntry(userId = userId, weight = 82f, date = LocalDate.of(2025, 12, 1)),
                WeightEntry(userId = userId, weight = 80f, date = LocalDate.of(2025, 12, 25)),
                WeightEntry(userId = userId, weight = 79f, date = LocalDate.of(2026, 1, 8)),
            ),
        )

        val trend = GetWeightTrendUseCase(repository)(userId, 7).first()

        assertEquals(79f, trend?.currentWeight)
        assertEquals(80f, trend?.referenceWeight)
    }
}
