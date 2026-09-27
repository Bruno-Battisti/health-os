package com.healthos.app.presentation.account

import java.time.Instant

data class AccountUiState(
    val isAuthenticated: Boolean = false,
    val accountId: String? = null,
    val email: String = "",
    val password: String = "",
    val isRegisterMode: Boolean = true,
    val isSubmitting: Boolean = false,
    val isSyncing: Boolean = false,
    val lastSyncedAt: Instant? = null,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
)
