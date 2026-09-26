package com.healthos.app.domain.repository

import com.healthos.app.domain.model.PersonalGoal
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun observeOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
    fun observePersonalGoal(): Flow<PersonalGoal?>
    suspend fun setPersonalGoal(goal: PersonalGoal)
}
