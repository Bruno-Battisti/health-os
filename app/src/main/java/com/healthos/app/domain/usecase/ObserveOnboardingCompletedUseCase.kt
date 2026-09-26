package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveOnboardingCompletedUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {
    operator fun invoke(): Flow<Boolean> = preferencesRepository.observeOnboardingCompleted()
}
