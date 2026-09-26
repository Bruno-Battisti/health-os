package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.HabitRepository
import javax.inject.Inject

class SeedDefaultHabitsUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
) {
    suspend operator fun invoke(userId: Long) = habitRepository.seedDefaultHabits(userId)
}
