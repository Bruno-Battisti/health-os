package com.healthos.app.presentation.weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthos.app.domain.model.User
import com.healthos.app.domain.usecase.AddWeightEntryUseCase
import com.healthos.app.domain.usecase.DeleteWeightEntryUseCase
import com.healthos.app.domain.usecase.GetWeightHistoryUseCase
import com.healthos.app.domain.usecase.GetWeightTrendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

private const val SEVEN_DAYS = 7
private const val THIRTY_DAYS = 30
private const val NINETY_DAYS = 90

@HiltViewModel
class WeightViewModel @Inject constructor(
    private val addWeightEntryUseCase: AddWeightEntryUseCase,
    private val deleteWeightEntryUseCase: DeleteWeightEntryUseCase,
    getWeightHistoryUseCase: GetWeightHistoryUseCase,
    getWeightTrendUseCase: GetWeightTrendUseCase,
) : ViewModel() {

    private val weightInput = MutableStateFlow("")
    private val noteInput = MutableStateFlow("")
    private val errorMessage = MutableStateFlow<String?>(null)
    private val isSaving = MutableStateFlow(false)

    private val history = getWeightHistoryUseCase(User.SINGLE_USER_ID)
    private val trend7 = getWeightTrendUseCase(User.SINGLE_USER_ID, SEVEN_DAYS)
    private val trend30 = getWeightTrendUseCase(User.SINGLE_USER_ID, THIRTY_DAYS)
    private val trend90 = getWeightTrendUseCase(User.SINGLE_USER_ID, NINETY_DAYS)

    val uiState: StateFlow<WeightUiState> = combine(
        weightInput, noteInput, errorMessage, isSaving, history,
    ) { weight, note, error, saving, hist ->
        WeightUiState(weightInput = weight, noteInput = note, errorMessage = error, isSaving = saving, history = hist)
    }.combine(trend7) { state, t7 -> state.copy(trend7 = t7) }
        .combine(trend30) { state, t30 -> state.copy(trend30 = t30) }
        .combine(trend90) { state, t90 -> state.copy(trend90 = t90) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, WeightUiState())

    fun onWeightChange(value: String) {
        weightInput.value = value
        errorMessage.value = null
    }

    fun onNoteChange(value: String) {
        noteInput.value = value
    }

    fun submit() {
        val weight = weightInput.value.toFloatOrNull()
        if (weight == null || weight <= 0f) {
            errorMessage.value = "Informe um peso válido"
            return
        }

        isSaving.value = true
        viewModelScope.launch {
            addWeightEntryUseCase(weight, LocalDate.now(), noteInput.value.ifBlank { null })
            weightInput.value = ""
            noteInput.value = ""
            isSaving.value = false
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch { deleteWeightEntryUseCase(id) }
    }
}
