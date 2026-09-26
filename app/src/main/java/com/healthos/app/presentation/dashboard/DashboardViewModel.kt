package com.healthos.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthos.app.domain.model.User
import com.healthos.app.domain.usecase.GetLatestWeightUseCase
import com.healthos.app.domain.usecase.GetUserProfileUseCase
import com.healthos.app.domain.usecase.GetWeightTrendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import javax.inject.Inject

private const val SEVEN_DAYS = 7

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getUserProfileUseCase: GetUserProfileUseCase,
    getLatestWeightUseCase: GetLatestWeightUseCase,
    getWeightTrendUseCase: GetWeightTrendUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getUserProfileUseCase(),
                getLatestWeightUseCase(User.SINGLE_USER_ID),
                getWeightTrendUseCase(User.SINGLE_USER_ID, SEVEN_DAYS),
            ) { user, latestWeight, trend7 ->
                DashboardUiState(
                    userName = user?.name,
                    userAge = user?.birthDate?.let { Period.between(it, LocalDate.now()).years },
                    currentWeight = latestWeight?.weight,
                    weightDelta7Days = trend7?.deltaKg,
                    isLoading = false,
                )
            }.collect { _uiState.value = it }
        }
    }
}
