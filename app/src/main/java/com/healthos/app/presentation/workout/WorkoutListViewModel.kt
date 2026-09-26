package com.healthos.app.presentation.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthos.app.domain.model.User
import com.healthos.app.domain.usecase.CreateWorkoutUseCase
import com.healthos.app.domain.usecase.DeleteWorkoutUseCase
import com.healthos.app.domain.usecase.GetWorkoutHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WorkoutListViewModel @Inject constructor(
    private val createWorkoutUseCase: CreateWorkoutUseCase,
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase,
    getWorkoutHistoryUseCase: GetWorkoutHistoryUseCase,
) : ViewModel() {

    private val isCreateDialogOpen = MutableStateFlow(false)
    private val newWorkoutName = MutableStateFlow("")
    private val history = getWorkoutHistoryUseCase(User.SINGLE_USER_ID)

    private val _createdWorkoutId = Channel<Long>(Channel.BUFFERED)
    val createdWorkoutId: Flow<Long> = _createdWorkoutId.receiveAsFlow()

    val uiState: StateFlow<WorkoutListUiState> = combine(
        history, isCreateDialogOpen, newWorkoutName,
    ) { hist, dialogOpen, name ->
        WorkoutListUiState(history = hist, isCreateDialogOpen = dialogOpen, newWorkoutName = name)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, WorkoutListUiState())

    fun openCreateDialog() {
        isCreateDialogOpen.value = true
    }

    fun dismissCreateDialog() {
        isCreateDialogOpen.value = false
        newWorkoutName.value = ""
    }

    fun onNewWorkoutNameChange(value: String) {
        newWorkoutName.value = value
    }

    fun confirmCreateWorkout() {
        val name = newWorkoutName.value.ifBlank { "Treino" }
        viewModelScope.launch {
            val id = createWorkoutUseCase(User.SINGLE_USER_ID, name, LocalDate.now())
            dismissCreateDialog()
            _createdWorkoutId.send(id)
        }
    }

    fun deleteWorkout(id: Long) {
        viewModelScope.launch { deleteWorkoutUseCase(id) }
    }
}
