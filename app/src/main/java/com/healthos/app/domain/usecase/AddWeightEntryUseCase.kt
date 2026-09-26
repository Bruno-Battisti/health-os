package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.User
import com.healthos.app.domain.model.WeightEntry
import com.healthos.app.domain.repository.WeightRepository
import java.time.LocalDate
import javax.inject.Inject

class AddWeightEntryUseCase @Inject constructor(
    private val weightRepository: WeightRepository,
) {
    suspend operator fun invoke(weight: Float, date: LocalDate, note: String? = null) {
        weightRepository.addEntry(WeightEntry(userId = User.SINGLE_USER_ID, weight = weight, date = date, note = note))
    }
}
