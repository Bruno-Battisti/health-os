package com.healthos.app.domain.usecase

import com.healthos.app.domain.model.PersonalGoal
import com.healthos.app.domain.repository.PreferencesRepository
import javax.inject.Inject

class SetPersonalGoalUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke(goal: PersonalGoal) = preferencesRepository.setPersonalGoal(goal)
}
