package com.healthos.app.presentation.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthos.app.domain.model.User
import com.healthos.app.domain.usecase.AddGoalUseCase
import com.healthos.app.domain.usecase.CheckInGoalUseCase
import com.healthos.app.domain.usecase.DeleteGoalUseCase
import com.healthos.app.domain.usecase.GetGoalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val addGoalUseCase: AddGoalUseCase,
    private val checkInGoalUseCase: CheckInGoalUseCase,
    private val deleteGoalUseCase: DeleteGoalUseCase,
    getGoalsUseCase: GetGoalsUseCase,
) : ViewModel() {

    private val isAddDialogOpen = MutableStateFlow(false)
    private val newGoalDescription = MutableStateFlow("")
    private val newGoalTargetCount = MutableStateFlow("")
    private val newGoalDeadline = MutableStateFlow("")
    private val goals = getGoalsUseCase(User.SINGLE_USER_ID)

    val uiState = combine(
        goals, isAddDialogOpen, newGoalDescription, newGoalTargetCount, newGoalDeadline,
    ) { goalList, dialogOpen, description, target, deadline ->
        GoalsUiState(goalList, dialogOpen, description, target, deadline)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, GoalsUiState())

    fun checkIn(goalId: Long) {
        viewModelScope.launch { checkInGoalUseCase(goalId, LocalDate.now()) }
    }

    fun deleteGoal(id: Long) {
        viewModelScope.launch { deleteGoalUseCase(id) }
    }

    fun openAddDialog() {
        isAddDialogOpen.value = true
    }

    fun dismissAddDialog() {
        isAddDialogOpen.value = false
        newGoalDescription.value = ""
        newGoalTargetCount.value = ""
        newGoalDeadline.value = ""
    }

    fun onDescriptionChange(value: String) {
        newGoalDescription.value = value
    }

    fun onTargetCountChange(value: String) {
        newGoalTargetCount.value = value
    }

    fun onDeadlineChange(value: String) {
        newGoalDeadline.value = value
    }

    fun confirmAddGoal() {
        val description = newGoalDescription.value.trim()
        val target = newGoalTargetCount.value.toIntOrNull()
        val deadline = newGoalDeadline.value.toLocalDateOrNull()
        if (description.isEmpty() || target == null || target <= 0 || deadline == null) return

        viewModelScope.launch {
            addGoalUseCase(User.SINGLE_USER_ID, description, target, deadline)
            dismissAddDialog()
        }
    }
}

private fun String.toLocalDateOrNull(): LocalDate? = try {
    LocalDate.parse(this)
} catch (e: DateTimeParseException) {
    null
}
