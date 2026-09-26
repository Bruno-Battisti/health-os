package com.healthos.app.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.healthos.app.data.local.AppDatabase
import com.healthos.app.data.local.entity.UserEntity
import com.healthos.app.domain.model.WeightEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class WeightRepositoryImplTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: WeightRepositoryImpl
    private val userId = 1L

    @Before
    fun setUp() = runTest {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        database.userDao().upsert(
            UserEntity(id = userId, name = "Ana", birthDate = 0, height = 165f, createdAt = 0),
        )
        repository = WeightRepositoryImpl(database.weightDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `observeLatest emits null when no entry was recorded`() = runTest {
        assertNull(repository.observeLatest(userId).first())
    }

    @Test
    fun `observeLatest returns the most recently added entry`() = runTest {
        repository.addEntry(WeightEntry(userId = userId, weight = 80f, date = LocalDate.of(2026, 1, 1)))
        repository.addEntry(WeightEntry(userId = userId, weight = 79f, date = LocalDate.of(2026, 1, 8)))

        val latest = repository.observeLatest(userId).first()

        assertEquals(79f, latest?.weight)
        assertEquals(LocalDate.of(2026, 1, 8), latest?.date)
    }
}
