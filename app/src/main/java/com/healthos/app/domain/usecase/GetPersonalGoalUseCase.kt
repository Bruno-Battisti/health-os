package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.PersonalGoal
import com.healthos.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPersonalGoalUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {
    operator fun invoke(): Flow<PersonalGoal?> = preferencesRepository.observePersonalGoal()
}
