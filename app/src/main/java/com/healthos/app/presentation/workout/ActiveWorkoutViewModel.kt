package com.healthos.app.presentation.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthos.app.domain.model.ExerciseSet
import com.healthos.app.domain.usecase.AddExerciseUseCase
import com.healthos.app.domain.usecase.AddSetUseCase
import com.healthos.app.domain.usecase.FinishWorkoutUseCase
import com.healthos.app.domain.usecase.GetWorkoutUseCase
import com.healthos.app.domain.usecase.ToggleSetCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

private const val WORKOUT_ID_KEY = "workoutId"

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addExerciseUseCase: AddExerciseUseCase,
    private val addSetUseCase: AddSetUseCase,
    private val toggleSetCompletedUseCase: ToggleSetCompletedUseCase,
    private val finishWorkoutUseCase: FinishWorkoutUseCase,
    getWorkoutUseCase: GetWorkoutUseCase,
) : ViewModel() {

    private val workoutId: Long = checkNotNull(savedStateHandle[WORKOUT_ID_KEY])
    private val sessionStart = Instant.now()

    private val newExerciseName = MutableStateFlow("")
    private val setInputs = MutableStateFlow<Map<Long, SetInput>>(emptyMap())
    private val restSecondsRemaining = MutableStateFlow(ActiveWorkoutUiState.DEFAULT_REST_SECONDS)
    private val isTimerRunning = MutableStateFlow(false)
    private val isFinished = MutableStateFlow(false)
    private var timerJob: Job? = null

    val uiState: StateFlow<ActiveWorkoutUiState> = combine(
        getWorkoutUseCase(workoutId), newExerciseName, setInputs, restSecondsRemaining, isTimerRunning, isFinished,
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        ActiveWorkoutUiState(
            workout = values[0] as com.healthos.app.domain.model.Workout?,
            newExerciseName = values[1] as String,
            setInputs = values[2] as Map<Long, SetInput>,
            restSecondsRemaining = values[3] as Int,
            isTimerRunning = values[4] as Boolean,
            isFinished = values[5] as Boolean,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ActiveWorkoutUiState())

    fun onNewExerciseNameChange(value: String) {
        newExerciseName.value = value
    }

    fun addExercise() {
        val name = newExerciseName.value.trim()
        if (name.isEmpty()) return

        viewModelScope.launch {
            val order = uiState.value.workout?.exercises?.size ?: 0
            addExerciseUseCase(workoutId, name, order)
            newExerciseName.value = ""
        }
    }

    fun onSetWeightChange(exerciseId: Long, value: String) {
        setInputs.value = setInputs.value + (exerciseId to (setInputs.value[exerciseId] ?: SetInput()).copy(weight = value))
    }

    fun onSetRepsChange(exerciseId: Long, value: String) {
        setInputs.value = setInputs.value + (exerciseId to (setInputs.value[exerciseId] ?: SetInput()).copy(repetitions = value))
    }

    fun addSet(exerciseId: Long) {
        val input = setInputs.value[exerciseId] ?: return
        val weight = input.weight.toFloatOrNull() ?: return
        val reps = input.repetitions.toIntOrNull() ?: return

        viewModelScope.launch {
            val order = uiState.value.workout?.exercises?.firstOrNull { it.id == exerciseId }?.sets?.size ?: 0
            addSetUseCase(exerciseId, weight, reps, order)
            setInputs.value = setInputs.value + (exerciseId to SetInput())
        }
    }

    fun toggleSetCompleted(set: ExerciseSet) {
        viewModelScope.launch { toggleSetCompletedUseCase(set) }
    }

    fun toggleTimer() {
        if (isTimerRunning.value) {
            timerJob?.cancel()
            isTimerRunning.value = false
        } else {
            isTimerRunning.value = true
            timerJob = viewModelScope.launch {
                while (restSecondsRemaining.value > 0) {
                    delay(1_000)
                    restSecondsRemaining.value -= 1
                }
                isTimerRunning.value = false
            }
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        isTimerRunning.value = false
        restSecondsRemaining.value = ActiveWorkoutUiState.DEFAULT_REST_SECONDS
    }

    fun finishWorkout() {
        viewModelScope.launch {
            val minutes = Duration.between(sessionStart, Instant.now()).toMinutes().toInt().coerceAtLeast(1)
            finishWorkoutUseCase(workoutId, minutes)
            isFinished.value = true
        }
    }
}
