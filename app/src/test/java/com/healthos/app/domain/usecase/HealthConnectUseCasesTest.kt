package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.HealthConnectRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HealthConnectUseCasesTest {

    private val repository = mockk<HealthConnectRepository>()

    @Test
    fun `GetHealthConnectStatusUseCase combines availability, permissions and required permission set`() = runTest {
        val permissions = setOf("android.permission.health.READ_STEPS")
        every { repository.requiredPermissions } returns permissions
        coEvery { repository.isAvailable() } returns true
        coEvery { repository.hasAllPermissions() } returns false

        val status = GetHealthConnectStatusUseCase(repository)()

        assertEquals(true, status.isAvailable)
        assertEquals(false, status.hasAllPermissions)
        assertEquals(permissions, status.requiredPermissions)
    }

    @Test
    fun `GetTodayStepsUseCase delegates to repository`() = runTest {
        coEvery { repository.readStepsToday() } returns 4200L

        assertEquals(4200L, GetTodayStepsUseCase(repository)())
    }

    @Test
    fun `GetLastNightSleepUseCase delegates to repository`() = runTest {
        coEvery { repository.readSleepMinutesLastNight() } returns 420L

        assertEquals(420L, GetLastNightSleepUseCase(repository)())
    }

    @Test
    fun `GetLastNightSleepUseCase returns null when there is no sleep session`() = runTest {
        coEvery { repository.readSleepMinutesLastNight() } returns null

        assertNull(GetLastNightSleepUseCase(repository)())
    }

    @Test
    fun `GetWeeklyExerciseSessionCountUseCase asks the repository for the last 7 days`() = runTest {
        coEvery { repository.readExerciseSessionCount(7) } returns 3

        assertEquals(3, GetWeeklyExerciseSessionCountUseCase(repository)())
    }
}
