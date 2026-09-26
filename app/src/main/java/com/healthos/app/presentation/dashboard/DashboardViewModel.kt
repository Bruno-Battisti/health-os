package com.healthos.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthos.app.domain.model.User
import com.healthos.app.domain.usecase.GetHabitProgressUseCase
import com.healthos.app.domain.usecase.GetHabitsUseCase
import com.healthos.app.domain.usecase.GetLatestWeightUseCase
import com.healthos.app.domain.usecase.GetUserProfileUseCase
import com.healthos.app.domain.usecase.GetWeightTrendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import javax.inject.Inject

private const val SEVEN_DAYS = 7
private const val WATER_HABIT_NAME = "Água"

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    getUserProfileUseCase: GetUserProfileUseCase,
    getLatestWeightUseCase: GetLatestWeightUseCase,
    getWeightTrendUseCase: GetWeightTrendUseCase,
    getHabitsUseCase: GetHabitsUseCase,
    getHabitProgressUseCase: GetHabitProgressUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        val waterHabit = getHabitsUseCase(User.SINGLE_USER_ID).map { it.find { h -> h.name == WATER_HABIT_NAME } }
        val waterToday = waterHabit.flatMapLatest { habit ->
            if (habit == null) flowOf(null) else getHabitProgressUseCase(habit.id, LocalDate.now())
        }
        val waterInfo = combine(waterHabit, waterToday) { habit, today -> (habit?.targetValue) to today }

        viewModelScope.launch {
            combine(
                getUserProfileUseCase(),
                getLatestWeightUseCase(User.SINGLE_USER_ID),
                getWeightTrendUseCase(User.SINGLE_USER_ID, SEVEN_DAYS),
                waterInfo,
            ) { user, latestWeight, trend7, water ->
                DashboardUiState(
                    userName = user?.name,
                    userAge = user?.birthDate?.let { Period.between(it, LocalDate.now()).years },
                    currentWeight = latestWeight?.weight,
                    weightDelta7Days = trend7?.deltaKg,
                    waterTarget = water.first,
                    waterToday = water.second,
                    isLoading = false,
                )
            }.collect { _uiState.value = it }
        }
    }
}
