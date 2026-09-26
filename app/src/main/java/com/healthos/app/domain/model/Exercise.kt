package com.healthos.app.domain.model

data class Exercise(
    val id: Long = 0,
    val workoutId: Long,
    val name: String,
    val order: Int,
    val sets: List<ExerciseSet> = emptyList(),
)
