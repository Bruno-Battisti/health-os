package com.healthos.app.presentation.habit

import com.healthos.app.domain.model.Habit

data class HabitDisplay(val habit: Habit, val todayValue: Float)
