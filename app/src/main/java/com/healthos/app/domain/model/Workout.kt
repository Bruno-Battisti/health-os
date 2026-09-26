package com.healthos.app.domain.model

import java.time.LocalDate

data class Workout(
    val id: Long = 0,
    val userId: Long,
    val name: String,
    val date: LocalDate,
    val durationMinutes: Int? = null,
    val exercises: List<Exercise> = emptyList(),
)
