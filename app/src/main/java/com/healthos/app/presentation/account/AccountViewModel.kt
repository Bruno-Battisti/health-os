package com.healthos.app.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthos.app.domain.usecase.LoginUseCase
import com.healthos.app.domain.usecase.LogoutUseCase
import com.healthos.app.domain.usecase.ObserveAuthStateUseCase
import com.healthos.app.domain.usecase.ObserveLastSyncedAtUseCase
import com.healthos.app.domain.usecase.RegisterUseCase
import com.healthos.app.domain.usecase.SyncNowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val syncNowUseCase: SyncNowUseCase,
    observeAuthStateUseCase: ObserveAuthStateUseCase,
    observeLastSyncedAtUseCase: ObserveLastSyncedAtUseCase,
) : ViewModel() {

    private val email = MutableStateFlow("")
    private val password = MutableStateFlow("")
    private val isRegisterMode = MutableStateFlow(true)
    private val isSubmitting = MutableStateFlow(false)
    private val isSyncing = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val infoMessage = MutableStateFlow<String?>(null)

    private val session = observeAuthStateUseCase()
    private val lastSyncedAt = observeLastSyncedAtUseCase()

    val uiState: StateFlow<AccountUiState> = combine(session, email, password, isRegisterMode) {
            session, email, password, registerMode ->
        AccountUiState(
            isAuthenticated = session != null,
            accountId = session?.accountId,
            email = email,
            password = password,
            isRegisterMode = registerMode,
        )
    }
        .combine(isSubmitting) { state, submitting -> state.copy(isSubmitting = submitting) }
        .combine(isSyncing) { state, syncing -> state.copy(isSyncing = syncing) }
        .combine(lastSyncedAt) { state, syncedAt -> state.copy(lastSyncedAt = syncedAt) }
        .combine(errorMessage) { state, error -> state.copy(errorMessage = error) }
        .combine(infoMessage) { state, info -> state.copy(infoMessage = info) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AccountUiState())

    fun onEmailChange(value: String) {
        email.value = value
        errorMessage.value = null
    }

    fun onPasswordChange(value: String) {
        password.value = value
        errorMessage.value = null
    }

    fun onToggleMode() {
        isRegisterMode.value = !isRegisterMode.value
        errorMessage.value = null
    }

    fun submit() {
        val currentEmail = email.value.trim()
        val currentPassword = password.value

        if (currentEmail.isBlank() || currentPassword.isBlank()) {
            errorMessage.value = "Informe e-mail e senha"
            return
        }

        isSubmitting.value = true
        errorMessage.value = null

        viewModelScope.launch {
            val result = if (isRegisterMode.value) {
                registerUseCase(currentEmail, currentPassword)
            } else {
                loginUseCase(currentEmail, currentPassword)
            }

            result
                .onSuccess {
                    password.value = ""
                    infoMessage.value = "Conectado com sucesso"
                }
                .onFailure { errorMessage.value = it.message ?: "Não foi possível conectar" }

            isSubmitting.value = false
        }
    }

    fun syncNow() {
        isSyncing.value = true
        infoMessage.value = null
        errorMessage.value = null

        viewModelScope.launch {
            syncNowUseCase()
                .onSuccess { infoMessage.value = "Sincronizado com sucesso" }
                .onFailure { errorMessage.value = it.message ?: "Falha ao sincronizar" }
            isSyncing.value = false
        }
    }

    fun logout() {
        logoutUseCase()
        email.value = ""
        password.value = ""
        infoMessage.value = null
        errorMessage.value = null
    }
}
