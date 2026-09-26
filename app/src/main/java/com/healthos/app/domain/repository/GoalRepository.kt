package com.healthos.app.domain.repository

import com.healthos.app.domain.model.GoalWithProgress
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface GoalRepository {
    suspend fun addGoal(userId: Long, description: String, targetCount: Int, deadline: LocalDate): Long
    suspend fun deleteGoal(id: Long)
    suspend fun checkIn(goalId: Long, date: LocalDate)

    fun observeGoals(userId: Long): Flow<List<GoalWithProgress>>
}
