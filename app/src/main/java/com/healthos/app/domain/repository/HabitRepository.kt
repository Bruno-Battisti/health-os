package com.healthos.app.domain.repository

import com.healthos.app.domain.model.Habit
import com.healthos.app.domain.model.HabitEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HabitRepository {
    suspend fun addHabit(userId: Long, name: String, targetValue: Float, unit: String): Long
    suspend fun deleteHabit(id: Long)
    suspend fun logEntry(habitId: Long, value: Float, date: LocalDate)
    suspend fun seedDefaultHabits(userId: Long)

    fun observeHabits(userId: Long): Flow<List<Habit>>
    fun observeEntries(habitId: Long): Flow<List<HabitEntry>>
    fun observeEntriesForDate(habitId: Long, date: LocalDate): Flow<List<HabitEntry>>
}
