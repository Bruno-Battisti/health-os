package com.healthos.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.healthos.app.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Insert
    suspend fun insert(goal: GoalEntity): Long

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY deadline ASC")
    fun observeByUser(userId: Long): Flow<List<GoalEntity>>
}
