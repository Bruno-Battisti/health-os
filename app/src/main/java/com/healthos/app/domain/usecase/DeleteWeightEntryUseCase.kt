package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.WeightRepository
import javax.inject.Inject

class DeleteWeightEntryUseCase @Inject constructor(
    private val weightRepository: WeightRepository,
) {
    suspend operator fun invoke(id: Long) = weightRepository.deleteEntry(id)
}
