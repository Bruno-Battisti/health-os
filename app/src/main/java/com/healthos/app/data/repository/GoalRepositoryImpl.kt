package com.healthos.app.data.repository

import com.healthos.app.data.local.dao.GoalCheckInDao
import com.healthos.app.data.local.dao.GoalDao
import com.healthos.app.data.local.entity.GoalCheckInEntity
import com.healthos.app.data.local.entity.GoalEntity
import com.healthos.app.domain.model.Goal
import com.healthos.app.domain.model.GoalWithProgress
import com.healthos.app.domain.repository.GoalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
    private val goalCheckInDao: GoalCheckInDao,
) : GoalRepository {

    override suspend fun addGoal(userId: Long, description: String, targetCount: Int, deadline: LocalDate): Long =
        goalDao.insert(
            GoalEntity(
                userId = userId,
                description = description,
                targetCount = targetCount,
                deadline = deadline.toEpochDay(),
                createdAt = Instant.now().toEpochMilli(),
            ),
        )

    override suspend fun deleteGoal(id: Long) {
        goalDao.delete(id)
    }

    override suspend fun checkIn(goalId: Long, date: LocalDate) {
        goalCheckInDao.insert(GoalCheckInEntity(goalId = goalId, date = date.toEpochDay()))
    }

    override fun observeGoals(userId: Long): Flow<List<GoalWithProgress>> =
        goalDao.observeByUser(userId).flatMapLatest { goals ->
            if (goals.isEmpty()) {
                flowOf(emptyList())
            } else {
                goalCheckInDao.observeByGoals(goals.map { it.id }).map { checkIns ->
                    val countByGoal = checkIns.groupingBy { it.goalId }.eachCount()
                    goals.map { goal -> GoalWithProgress(goal.toDomain(), countByGoal[goal.id] ?: 0) }
                }
            }
        }
}

private fun GoalEntity.toDomain(): Goal = Goal(
    id = id,
    userId = userId,
    description = description,
    targetCount = targetCount,
    deadline = LocalDate.ofEpochDay(deadline),
    createdAt = Instant.ofEpochMilli(createdAt),
)
