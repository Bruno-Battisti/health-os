package com.healthos.app.presentation.habit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthos.app.domain.model.User
import com.healthos.app.domain.usecase.AddHabitUseCase
import com.healthos.app.domain.usecase.DeleteHabitUseCase
import com.healthos.app.domain.usecase.GetHabitProgressUseCase
import com.healthos.app.domain.usecase.GetHabitsUseCase
import com.healthos.app.domain.usecase.LogHabitEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

private data class NewHabitForm(
    val isDialogOpen: Boolean = false,
    val name: String = "",
    val target: String = "",
    val unit: String = "",
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val addHabitUseCase: AddHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val logHabitEntryUseCase: LogHabitEntryUseCase,
    getHabitsUseCase: GetHabitsUseCase,
    getHabitProgressUseCase: GetHabitProgressUseCase,
) : ViewModel() {

    private val logInputs = MutableStateFlow<Map<Long, String>>(emptyMap())
    private val newHabitForm = MutableStateFlow(NewHabitForm())

    private val habitsWithProgress = getHabitsUseCase(User.SINGLE_USER_ID).flatMapLatest { habits ->
        if (habits.isEmpty()) {
            flowOf(emptyList())
        } else {
            combine(
                habits.map { habit ->
                    getHabitProgressUseCase(habit.id, LocalDate.now()).map { progress -> HabitDisplay(habit, progress) }
                },
            ) { it.toList() }
        }
    }

    val uiState = combine(habitsWithProgress, logInputs, newHabitForm) { habits, inputs, form ->
        HabitsUiState(habits, inputs, form.isDialogOpen, form.name, form.target, form.unit)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HabitsUiState())

    fun onLogInputChange(habitId: Long, value: String) {
        logInputs.value = logInputs.value + (habitId to value)
    }

    fun logEntry(habitId: Long) {
        val value = logInputs.value[habitId]?.toFloatOrNull() ?: return
        viewModelScope.launch {
            logHabitEntryUseCase(habitId, value, LocalDate.now())
            logInputs.value = logInputs.value + (habitId to "")
        }
    }

    fun deleteHabit(id: Long) {
        viewModelScope.launch { deleteHabitUseCase(id) }
    }

    fun openAddDialog() {
        newHabitForm.value = newHabitForm.value.copy(isDialogOpen = true)
    }

    fun dismissAddDialog() {
        newHabitForm.value = NewHabitForm()
    }

    fun onNewHabitNameChange(value: String) {
        newHabitForm.value = newHabitForm.value.copy(name = value)
    }

    fun onNewHabitTargetChange(value: String) {
        newHabitForm.value = newHabitForm.value.copy(target = value)
    }

    fun onNewHabitUnitChange(value: String) {
        newHabitForm.value = newHabitForm.value.copy(unit = value)
    }

    fun confirmAddHabit() {
        val form = newHabitForm.value
        val name = form.name.trim()
        val target = form.target.toFloatOrNull()
        if (name.isEmpty() || target == null || target <= 0f) return

        viewModelScope.launch {
            addHabitUseCase(User.SINGLE_USER_ID, name, target, form.unit.trim())
            dismissAddDialog()
        }
    }
}
