package com.healthos.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.healthos.app.data.local.entity.HabitEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitEntryDao {

    @Insert
    suspend fun insert(entry: HabitEntryEntity): Long

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId ORDER BY date DESC")
    fun observeByHabit(habitId: Long): Flow<List<HabitEntryEntity>>

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId AND date = :date")
    fun observeByHabitAndDate(habitId: Long, date: Long): Flow<List<HabitEntryEntity>>
}
