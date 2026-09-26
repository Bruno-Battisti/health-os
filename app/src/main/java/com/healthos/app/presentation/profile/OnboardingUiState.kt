package com.healthos.app.presentation.profile

import com.healthos.app.domain.model.PersonalGoal

data class OnboardingUiState(
    val name: String = "",
    val birthDate: String = "",
    val heightCm: String = "",
    val initialWeight: String = "",
    val goal: PersonalGoal? = null,
    val isSaving: Boolean = false,
    val isComplete: Boolean = false,
    val errorMessage: String? = null,
)
