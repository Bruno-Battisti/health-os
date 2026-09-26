package com.healthos.app.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.healthos.app.data.local.AppDatabase
import com.healthos.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class HabitRepositoryImplTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: HabitRepositoryImpl
    private val userId = 1L

    @Before
    fun setUp() = runTest {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        database.userDao().upsert(UserEntity(id = userId, name = "Ana", birthDate = 0, height = 165f, createdAt = 0))
        repository = HabitRepositoryImpl(database.habitDao(), database.habitEntryDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `seedDefaultHabits creates agua and sono only once`() = runTest {
        repository.seedDefaultHabits(userId)
        repository.seedDefaultHabits(userId)

        val habits = repository.observeHabits(userId).first()

        assertEquals(2, habits.size)
        assertTrue(habits.any { it.name == "Água" })
        assertTrue(habits.any { it.name == "Sono" })
    }

    @Test
    fun `logEntry accumulates values for the same date`() = runTest {
        val habitId = repository.addHabit(userId, "Água", 2.5f, "L")

        repository.logEntry(habitId, 0.5f, LocalDate.of(2026, 1, 1))
        repository.logEntry(habitId, 0.3f, LocalDate.of(2026, 1, 1))

        val entries = repository.observeEntriesForDate(habitId, LocalDate.of(2026, 1, 1)).first()

        assertEquals(2, entries.size)
        assertEquals(0.8f, entries.sumOf { it.value.toDouble() }.toFloat())
    }

    @Test
    fun `deleteHabit removes it from the list`() = runTest {
        val habitId = repository.addHabit(userId, "Água", 2.5f, "L")

        repository.deleteHabit(habitId)

        assertTrue(repository.observeHabits(userId).first().isEmpty())
    }
}
