package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.Measurement
import com.healthos.app.domain.model.MeasurementType
import com.healthos.app.domain.model.User
import com.healthos.app.domain.repository.MeasurementRepository
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

class MeasurementUseCasesTest {

    private val repository = mockk<MeasurementRepository>()

    @Test
    fun `AddMeasurementUseCase stores a measurement for the single user`() = runTest {
        val date = LocalDate.of(2026, 1, 1)
        val expected = Measurement(userId = User.SINGLE_USER_ID, type = MeasurementType.ARM, value = 30f, date = date)
        coEvery { repository.addMeasurement(expected) } returns Unit

        AddMeasurementUseCase(repository)(MeasurementType.ARM, 30f, date)

        coVerify(exactly = 1) { repository.addMeasurement(expected) }
    }

    @Test
    fun `GetMeasurementHistoryUseCase returns the repository flow for the given type`() = runTest {
        val history = listOf(
            Measurement(userId = 1L, type = MeasurementType.WAIST, value = 80f, date = LocalDate.of(2026, 1, 1)),
        )
        every { repository.observeHistory(1L, MeasurementType.WAIST) } returns flowOf(history)

        assertEquals(history, GetMeasurementHistoryUseCase(repository)(1L, MeasurementType.WAIST).first())
    }

    @Test
    fun `DeleteMeasurementUseCase delegates to repository`() = runTest {
        coEvery { repository.deleteMeasurement(1L) } returns Unit

        DeleteMeasurementUseCase(repository)(1L)

        coVerify(exactly = 1) { repository.deleteMeasurement(1L) }
    }
}
