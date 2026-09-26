package com.healthos.app.presentation.healthconnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthos.app.domain.usecase.GetHealthConnectStatusUseCase
import com.healthos.app.domain.usecase.GetLastNightSleepUseCase
import com.healthos.app.domain.usecase.GetTodayStepsUseCase
import com.healthos.app.domain.usecase.GetWeeklyExerciseSessionCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthConnectViewModel @Inject constructor(
    private val getHealthConnectStatusUseCase: GetHealthConnectStatusUseCase,
    private val getTodayStepsUseCase: GetTodayStepsUseCase,
    private val getLastNightSleepUseCase: GetLastNightSleepUseCase,
    private val getWeeklyExerciseSessionCountUseCase: GetWeeklyExerciseSessionCountUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthConnectUiState())
    val uiState: StateFlow<HealthConnectUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val status = getHealthConnectStatusUseCase()

            if (!status.hasAllPermissions) {
                _uiState.value = HealthConnectUiState(
                    isAvailable = status.isAvailable,
                    hasPermissions = false,
                    requiredPermissions = status.requiredPermissions,
                    isLoading = false,
                )
                return@launch
            }

            _uiState.value = HealthConnectUiState(
                isAvailable = status.isAvailable,
                hasPermissions = true,
                requiredPermissions = status.requiredPermissions,
                steps = getTodayStepsUseCase(),
                sleepMinutes = getLastNightSleepUseCase(),
                exerciseSessionsThisWeek = getWeeklyExerciseSessionCountUseCase(),
                isLoading = false,
            )
        }
    }

    fun onPermissionsResult() {
        refresh()
    }
}
