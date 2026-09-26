package com.healthos.app.data.repository

import com.healthos.app.data.local.dao.HabitDao
import com.healthos.app.data.local.dao.HabitEntryDao
import com.healthos.app.data.local.entity.HabitEntity
import com.healthos.app.data.local.entity.HabitEntryEntity
import com.healthos.app.domain.model.Habit
import com.healthos.app.domain.model.HabitEntry
import com.healthos.app.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val habitEntryDao: HabitEntryDao,
) : HabitRepository {

    override suspend fun addHabit(userId: Long, name: String, targetValue: Float, unit: String): Long =
        habitDao.insert(HabitEntity(userId = userId, name = name, targetValue = targetValue, unit = unit))

    override suspend fun deleteHabit(id: Long) {
        habitDao.delete(id)
    }

    override suspend fun logEntry(habitId: Long, value: Float, date: LocalDate) {
        habitEntryDao.insert(HabitEntryEntity(habitId = habitId, value = value, date = date.toEpochDay()))
    }

    override suspend fun seedDefaultHabits(userId: Long) {
        if (habitDao.countForUser(userId) > 0) return
        habitDao.insert(HabitEntity(userId = userId, name = "Água", targetValue = 2.5f, unit = "L"))
        habitDao.insert(HabitEntity(userId = userId, name = "Sono", targetValue = 8f, unit = "h"))
    }

    override fun observeHabits(userId: Long): Flow<List<Habit>> =
        habitDao.observeByUser(userId).map { habits -> habits.map { it.toDomain() } }

    override fun observeEntries(habitId: Long): Flow<List<HabitEntry>> =
        habitEntryDao.observeByHabit(habitId).map { entries -> entries.map { it.toDomain() } }

    override fun observeEntriesForDate(habitId: Long, date: LocalDate): Flow<List<HabitEntry>> =
        habitEntryDao.observeByHabitAndDate(habitId, date.toEpochDay()).map { entries -> entries.map { it.toDomain() } }
}

private fun HabitEntity.toDomain(): Habit = Habit(id = id, userId = userId, name = name, targetValue = targetValue, unit = unit)

private fun HabitEntryEntity.toDomain(): HabitEntry = HabitEntry(id = id, habitId = habitId, value = value, date = LocalDate.ofEpochDay(date))
