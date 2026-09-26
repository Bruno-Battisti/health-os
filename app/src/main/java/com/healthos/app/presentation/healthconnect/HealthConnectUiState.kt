package com.healthos.app.presentation.healthconnect

data class HealthConnectUiState(
    val isAvailable: Boolean = false,
    val hasPermissions: Boolean = false,
    val requiredPermissions: Set<String> = emptySet(),
    val steps: Long? = null,
    val sleepMinutes: Long? = null,
    val exerciseSessionsThisWeek: Int? = null,
    val isLoading: Boolean = true,
)
