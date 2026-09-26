package com.healthos.app.presentation.dashboard

data class DashboardUiState(
    val userName: String? = null,
    val userAge: Int? = null,
    val currentWeight: Float? = null,
    val weightDelta7Days: Float? = null,
    val isLoading: Boolean = true,
)
