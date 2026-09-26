package com.healthos.app.presentation.dashboard

data class DashboardUiState(
    val userName: String? = null,
    val userAge: Int? = null,
    val currentWeight: Float? = null,
    val weightDelta7Days: Float? = null,
    val waterToday: Float? = null,
    val waterTarget: Float? = null,
    val steps: Long? = null,
    val isLoading: Boolean = true,
)
