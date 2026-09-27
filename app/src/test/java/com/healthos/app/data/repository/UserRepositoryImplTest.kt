package com.healthos.app.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.healthos.app.data.local.AppDatabase
import com.healthos.app.domain.model.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class UserRepositoryImplTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        repository = UserRepositoryImpl(database.userDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `observeUser emits null when no user was saved`() = runTest {
        assertNull(repository.observeUser().first())
    }

    @Test
    fun `saveUser then observeUser returns the saved profile`() = runTest {
        val user = User(
            name = "Ana",
            birthDate = LocalDate.of(1995, 4, 10),
            heightCm = 165f,
            createdAt = Instant.ofEpochSecond(1_700_000_000),
            updatedAt = Instant.ofEpochSecond(1_700_000_000),
        )

        repository.saveUser(user)
        val loaded = repository.observeUser().first()

        assertEquals(user, loaded)
    }

    @Test
    fun `saveUser twice replaces the single profile row`() = runTest {
        val first = User(
            name = "Ana",
            birthDate = LocalDate.of(1995, 4, 10),
            heightCm = 165f,
            createdAt = Instant.ofEpochSecond(1_700_000_000),
            updatedAt = Instant.ofEpochSecond(1_700_000_000),
        )
        val updated = first.copy(name = "Ana Paula", heightCm = 166f)

        repository.saveUser(first)
        repository.saveUser(updated)

        assertEquals(updated, repository.observeUser().first())
    }
}
