package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.PreferencesRepository
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke() = preferencesRepository.setOnboardingCompleted(true)
}
