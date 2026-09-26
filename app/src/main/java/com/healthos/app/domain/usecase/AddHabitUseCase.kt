package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.HabitRepository
import javax.inject.Inject

class AddHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
) {
    suspend operator fun invoke(userId: Long, name: String, targetValue: Float, unit: String): Long =
        habitRepository.addHabit(userId, name, targetValue, unit)
}
