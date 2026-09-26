package com.healthos.app.presentation.habit

data class HabitsUiState(
    val habits: List<HabitDisplay> = emptyList(),
    val logInputs: Map<Long, String> = emptyMap(),
    val isAddDialogOpen: Boolean = false,
    val newHabitName: String = "",
    val newHabitTarget: String = "",
    val newHabitUnit: String = "",
)
