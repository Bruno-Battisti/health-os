package com.healthos.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.healthos.app.domain.model.PersonalGoal
import com.healthos.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private object PreferencesKeys {
    val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    val PERSONAL_GOAL = stringPreferencesKey("personal_goal")
}

class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : PreferencesRepository {

    override fun observeOnboardingCompleted(): Flow<Boolean> =
        dataStore.data.map { it[PreferencesKeys.ONBOARDING_COMPLETED] ?: false }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[PreferencesKeys.ONBOARDING_COMPLETED] = completed }
    }

    override fun observePersonalGoal(): Flow<PersonalGoal?> =
        dataStore.data.map { preferences ->
            preferences[PreferencesKeys.PERSONAL_GOAL]?.let { PersonalGoal.valueOf(it) }
        }

    override suspend fun setPersonalGoal(goal: PersonalGoal) {
        dataStore.edit { it[PreferencesKeys.PERSONAL_GOAL] = goal.name }
    }
}
