package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.Habit
import com.healthos.app.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitsUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
) {
    operator fun invoke(userId: Long): Flow<List<Habit>> = habitRepository.observeHabits(userId)
}
