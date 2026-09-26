package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.WeightEntry
import com.healthos.app.domain.repository.WeightRepository
import javax.inject.Inject

class UpdateWeightEntryUseCase @Inject constructor(
    private val weightRepository: WeightRepository,
) {
    suspend operator fun invoke(entry: WeightEntry) = weightRepository.updateEntry(entry)
}
