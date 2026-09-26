package com.healthos.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.healthos.app.data.local.entity.GoalCheckInEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalCheckInDao {

    @Insert
    suspend fun insert(checkIn: GoalCheckInEntity): Long

    @Query("SELECT * FROM goal_check_ins WHERE goalId = :goalId ORDER BY date DESC")
    fun observeByGoal(goalId: Long): Flow<List<GoalCheckInEntity>>

    @Query("SELECT * FROM goal_check_ins WHERE goalId IN (:goalIds)")
    fun observeByGoals(goalIds: List<Long>): Flow<List<GoalCheckInEntity>>
}
