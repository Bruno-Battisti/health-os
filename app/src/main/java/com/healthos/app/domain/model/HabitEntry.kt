package com.healthos.app.domain.model

import java.time.LocalDate

data class HabitEntry(
    val id: Long = 0,
    val habitId: Long,
    val value: Float,
    val date: LocalDate,
)
