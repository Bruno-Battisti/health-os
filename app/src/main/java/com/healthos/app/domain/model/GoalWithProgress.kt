package com.healthos.app.domain.model

data class GoalWithProgress(
    val goal: Goal,
    val checkInCount: Int,
)
