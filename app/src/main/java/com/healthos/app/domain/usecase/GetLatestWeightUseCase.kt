package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.WeightEntry
import com.healthos.app.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLatestWeightUseCase @Inject constructor(
    private val weightRepository: WeightRepository,
) {
    operator fun invoke(userId: Long): Flow<WeightEntry?> = weightRepository.observeLatest(userId)
}
