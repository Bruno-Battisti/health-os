package com.healthos.app.presentation.profile

import com.healthos.app.domain.model.PersonalGoal
import java.time.Instant

data class ProfileUiState(
    val name: String = "",
    val birthDate: String = "",
    val heightCm: String = "",
    val goal: PersonalGoal? = null,
    val originalCreatedAt: Instant? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
)
