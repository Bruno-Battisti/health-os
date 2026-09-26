package com.healthos.app.domain.model

data class Habit(
    val id: Long = 0,
    val userId: Long,
    val name: String,
    val targetValue: Float,
    val unit: String,
)
