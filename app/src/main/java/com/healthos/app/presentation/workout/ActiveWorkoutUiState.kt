package com.healthos.app.presentation.workout

import com.healthos.app.domain.model.Workout

data class SetInput(val weight: String = "", val repetitions: String = "")

data class ActiveWorkoutUiState(
    val workout: Workout? = null,
    val newExerciseName: String = "",
    val setInputs: Map<Long, SetInput> = emptyMap(),
    val restSecondsRemaining: Int = DEFAULT_REST_SECONDS,
    val isTimerRunning: Boolean = false,
    val isFinished: Boolean = false,
) {
    companion object {
        const val DEFAULT_REST_SECONDS = 60
    }
}
