package com.healthos.app.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.healthos.app.data.local.AppDatabase
import com.healthos.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class GoalRepositoryImplTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: GoalRepositoryImpl
    private val userId = 1L

    @Before
    fun setUp() = runTest {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        database.userDao().upsert(UserEntity(id = userId, name = "Ana", birthDate = 0, height = 165f, createdAt = 0))
        repository = GoalRepositoryImpl(database.goalDao(), database.goalCheckInDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `a new goal has zero check-ins`() = runTest {
        repository.addGoal(userId, "Treinar 4x por semana", 4, LocalDate.of(2026, 12, 31))

        val goals = repository.observeGoals(userId).first()

        assertEquals(1, goals.size)
        assertEquals(0, goals.first().checkInCount)
    }

    @Test
    fun `checkIn increases the progress count for that goal only`() = runTest {
        val goalId = repository.addGoal(userId, "Treinar 4x por semana", 4, LocalDate.of(2026, 12, 31))
        repository.addGoal(userId, "Beber 2,5L por dia", 30, LocalDate.of(2026, 12, 31))

        repository.checkIn(goalId, LocalDate.of(2026, 1, 1))
        repository.checkIn(goalId, LocalDate.of(2026, 1, 2))

        val goals = repository.observeGoals(userId).first()

        assertEquals(2, goals.first { it.goal.id == goalId }.checkInCount)
        assertEquals(0, goals.first { it.goal.id != goalId }.checkInCount)
    }

    @Test
    fun `deleteGoal removes it from the list`() = runTest {
        val goalId = repository.addGoal(userId, "Treinar 4x por semana", 4, LocalDate.of(2026, 12, 31))

        repository.deleteGoal(goalId)

        assertEquals(emptyList<Any>(), repository.observeGoals(userId).first())
    }
}
