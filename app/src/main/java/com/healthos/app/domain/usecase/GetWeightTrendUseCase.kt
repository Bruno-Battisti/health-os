package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.WeightTrend
import com.healthos.app.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetWeightTrendUseCase @Inject constructor(
    private val weightRepository: WeightRepository,
) {
    operator fun invoke(userId: Long, periodDays: Int): Flow<WeightTrend?> =
        weightRepository.observeHistory(userId).map { history ->
            val current = history.maxByOrNull { it.date } ?: return@map null
            val cutoff = current.date.minusDays(periodDays.toLong())
            val reference = history.filter { it.date <= cutoff }.maxByOrNull { it.date }

            WeightTrend(
                currentWeight = current.weight,
                referenceWeight = reference?.weight,
                periodDays = periodDays,
            )
        }
}
