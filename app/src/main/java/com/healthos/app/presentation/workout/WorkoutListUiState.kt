package com.healthos.app.presentation.workout

import com.healthos.app.domain.model.Workout

data class WorkoutListUiState(
    val history: List<Workout> = emptyList(),
    val isCreateDialogOpen: Boolean = false,
    val newWorkoutName: String = "",
)
