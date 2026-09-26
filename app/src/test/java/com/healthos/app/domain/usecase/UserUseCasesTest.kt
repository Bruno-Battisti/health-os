package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.User
import com.healthos.app.domain.repository.UserRepository
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

class UserUseCasesTest {

    private val repository = mockk<UserRepository>()
    private val user = User(
        name = "Ana",
        birthDate = LocalDate.of(1995, 4, 10),
        heightCm = 165f,
        createdAt = Instant.ofEpochSecond(1_700_000_000),
    )

    @Test
    fun `SaveUserProfileUseCase delegates to repository`() = runTest {
        coEvery { repository.saveUser(user) } returns Unit

        SaveUserProfileUseCase(repository)(user)

        coVerify(exactly = 1) { repository.saveUser(user) }
    }

    @Test
    fun `GetUserProfileUseCase returns the repository flow`() = runTest {
        every { repository.observeUser() } returns flowOf(user)

        val result = GetUserProfileUseCase(repository)().first()

        assertEquals(user, result)
    }
}
