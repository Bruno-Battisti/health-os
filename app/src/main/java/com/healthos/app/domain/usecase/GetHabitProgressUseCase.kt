package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class GetHabitProgressUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
) {
    operator fun invoke(habitId: Long, date: LocalDate): Flow<Float> =
        habitRepository.observeEntriesForDate(habitId, date).map { entries -> entries.sumOf { it.value.toDouble() }.toFloat() }
}
