package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.HabitRepository
import java.time.LocalDate
import javax.inject.Inject

class LogHabitEntryUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
) {
    suspend operator fun invoke(habitId: Long, value: Float, date: LocalDate) =
        habitRepository.logEntry(habitId, value, date)
}
