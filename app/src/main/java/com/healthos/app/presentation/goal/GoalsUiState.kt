package com.healthos.app.presentation.goal

import com.healthos.app.domain.model.GoalWithProgress

data class GoalsUiState(
    val goals: List<GoalWithProgress> = emptyList(),
    val isAddDialogOpen: Boolean = false,
    val newGoalDescription: String = "",
    val newGoalTargetCount: String = "",
    val newGoalDeadline: String = "",
)
