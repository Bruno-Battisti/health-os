package com.healthos.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthos.app.domain.model.PersonalGoal
import com.healthos.app.domain.model.User
import com.healthos.app.domain.usecase.GetPersonalGoalUseCase
import com.healthos.app.domain.usecase.GetUserProfileUseCase
import com.healthos.app.domain.usecase.SaveUserProfileUseCase
import com.healthos.app.domain.usecase.SetPersonalGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getPersonalGoalUseCase: GetPersonalGoalUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    private val setPersonalGoalUseCase: SetPersonalGoalUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val user = getUserProfileUseCase().first()
            val goal = getPersonalGoalUseCase().first()
            _uiState.value = _uiState.value.copy(
                name = user?.name.orEmpty(),
                birthDate = user?.birthDate?.toString().orEmpty(),
                heightCm = user?.heightCm?.toString().orEmpty(),
                goal = goal,
                originalCreatedAt = user?.createdAt,
                isLoading = false,
            )
        }
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, errorMessage = null, saveSuccess = false)
    }

    fun onBirthDateChange(value: String) {
        _uiState.value = _uiState.value.copy(birthDate = value, errorMessage = null, saveSuccess = false)
    }

    fun onHeightChange(value: String) {
        _uiState.value = _uiState.value.copy(heightCm = value, errorMessage = null, saveSuccess = false)
    }

    fun onGoalChange(goal: PersonalGoal) {
        _uiState.value = _uiState.value.copy(goal = goal, errorMessage = null, saveSuccess = false)
    }

    fun save() {
        val state = _uiState.value
        val birthDate = state.birthDate.toLocalDateOrNull()
        val heightCm = state.heightCm.toFloatOrNull()

        val error = when {
            state.name.isBlank() -> "Informe seu nome"
            birthDate == null -> "Informe uma data de nascimento válida (AAAA-MM-DD)"
            heightCm == null || heightCm <= 0f -> "Informe uma altura válida"
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
                    createdAt = state.originalCreatedAt ?: Instant.now(),
                ),
            )
            state.goal?.let { setPersonalGoalUseCase(it) }

            _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
        }
    }
}

private fun String.toLocalDateOrNull(): LocalDate? = try {
    LocalDate.parse(this)
} catch (e: DateTimeParseException) {
    null
}
