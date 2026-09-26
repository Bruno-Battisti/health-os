package com.healthos.app.presentation.evolution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthos.app.domain.model.User
import com.healthos.app.domain.usecase.GetWeightHistoryUseCase
import com.healthos.app.domain.usecase.GetWeightTrendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val THIRTY_DAYS = 30

@HiltViewModel
class EvolutionViewModel @Inject constructor(
    getWeightHistoryUseCase: GetWeightHistoryUseCase,
    getWeightTrendUseCase: GetWeightTrendUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EvolutionUiState())
    val uiState: StateFlow<EvolutionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getWeightHistoryUseCase(User.SINGLE_USER_ID),
                getWeightTrendUseCase(User.SINGLE_USER_ID, THIRTY_DAYS),
            ) { history, trend30 ->
                EvolutionUiState(
                    history = history.sortedBy { it.date },
                    trend30 = trend30,
                    isLoading = false,
                )
            }.collect { _uiState.value = it }
        }
    }
}
