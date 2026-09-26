package com.healthos.app.domain.model

data class ExerciseSet(
    val id: Long = 0,
    val exerciseId: Long,
    val weight: Float,
    val repetitions: Int,
    val completed: Boolean,
    val order: Int,
)
