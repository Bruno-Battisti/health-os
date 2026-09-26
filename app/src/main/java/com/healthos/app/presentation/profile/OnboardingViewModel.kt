package com.healthos.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthos.app.domain.model.PersonalGoal
import com.healthos.app.domain.model.User
import com.healthos.app.domain.usecase.CompleteOnboardingUseCase
import com.healthos.app.domain.usecase.RecordInitialWeightUseCase
import com.healthos.app.domain.usecase.SaveUserProfileUseCase
import com.healthos.app.domain.usecase.SetPersonalGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    private val setPersonalGoalUseCase: SetPersonalGoalUseCase,
    private val recordInitialWeightUseCase: RecordInitialWeightUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, errorMessage = null)
    }

    fun onBirthDateChange(value: String) {
        _uiState.value = _uiState.value.copy(birthDate = value, errorMessage = null)
    }

    fun onHeightChange(value: String) {
        _uiState.value = _uiState.value.copy(heightCm = value, errorMessage = null)
    }

    fun onInitialWeightChange(value: String) {
        _uiState.value = _uiState.value.copy(initialWeight = value, errorMessage = null)
    }

    fun onGoalChange(goal: PersonalGoal) {
        _uiState.value = _uiState.value.copy(goal = goal, errorMessage = null)
    }

    fun submit() {
        val state = _uiState.value
        val birthDate = state.birthDate.toLocalDateOrNull()
        val heightCm = state.heightCm.toFloatOrNull()
        val initialWeight = state.initialWeight.toFloatOrNull()

        val error = when {
            state.name.isBlank() -> "Informe seu nome"
            birthDate == null -> "Informe uma data de nascimento válida (AAAA-MM-DD)"
            heightCm == null || heightCm <= 0f -> "Informe uma altura válida"
            initialWeight == null || initialWeight <= 0f -> "Informe um peso inicial válido"
            state.goal == null -> "Selecione um objetivo"
            else -> null
        }

        if (error != null) {
            _uiState.value = state.copy(errorMessage = error)
            return
        }

        _uiState.value = state.copy(isSaving = true, errorMessage = null)

        viewModelScope.launch {
            saveUserProfileUseCase(
                User(
                    name = state.name.trim(),
                    birthDate = birthDate!!,
                    heightCm = heightCm!!,
                    createdAt = Instant.now(),
                ),
            )
            recordInitialWeightUseCase(initialWeight!!, LocalDate.now())
            setPersonalGoalUseCase(state.goal!!)
            completeOnboardingUseCase()

            _uiState.value = _uiState.value.copy(isSaving = false, isComplete = true)
        }
    }
}

private fun String.toLocalDateOrNull(): LocalDate? = try {
    LocalDate.parse(this)
} catch (e: DateTimeParseException) {
    null
}
