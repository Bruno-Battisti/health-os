package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.HabitRepository
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
) {
    suspend operator fun invoke(id: Long) = habitRepository.deleteHabit(id)
}
