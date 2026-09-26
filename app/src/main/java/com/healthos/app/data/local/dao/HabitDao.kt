package com.healthos.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.healthos.app.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert
    suspend fun insert(habit: HabitEntity): Long

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM habits WHERE userId = :userId ORDER BY id ASC")
    fun observeByUser(userId: Long): Flow<List<HabitEntity>>

    @Query("SELECT COUNT(*) FROM habits WHERE userId = :userId")
    suspend fun countForUser(userId: Long): Int
}
