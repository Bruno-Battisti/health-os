package com.healthos.app.presentation.weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthos.app.domain.model.MeasurementType
import com.healthos.app.domain.model.User
import com.healthos.app.domain.usecase.AddMeasurementUseCase
import com.healthos.app.domain.usecase.DeleteMeasurementUseCase
import com.healthos.app.domain.usecase.GetMeasurementHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MeasurementViewModel @Inject constructor(
    private val addMeasurementUseCase: AddMeasurementUseCase,
    private val deleteMeasurementUseCase: DeleteMeasurementUseCase,
    getMeasurementHistoryUseCase: GetMeasurementHistoryUseCase,
) : ViewModel() {

    private val selectedType = MutableStateFlow(MeasurementType.ARM)
    private val valueInput = MutableStateFlow("")
    private val errorMessage = MutableStateFlow<String?>(null)

    private val history = selectedType.flatMapLatest { type ->
        getMeasurementHistoryUseCase(User.SINGLE_USER_ID, type)
    }

    val uiState: StateFlow<MeasurementUiState> = combine(
        selectedType, valueInput, errorMessage, history,
    ) { type, value, error, hist ->
        MeasurementUiState(selectedType = type, valueInput = value, errorMessage = error, history = hist)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MeasurementUiState())

    fun onTypeChange(type: MeasurementType) {
        selectedType.value = type
        valueInput.value = ""
        errorMessage.value = null
    }

    fun onValueChange(value: String) {
        valueInput.value = value
        errorMessage.value = null
    }

    fun submit() {
        val value = valueInput.value.toFloatOrNull()
        if (value == null || value <= 0f) {
            errorMessage.value = "Informe um valor válido"
            return
        }

        viewModelScope.launch {
            addMeasurementUseCase(selectedType.value, value, LocalDate.now())
            valueInput.value = ""
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch { deleteMeasurementUseCase(id) }
    }
}
