package com.healthos.app.domain.model

import java.time.LocalDate

data class GoalCheckIn(
    val id: Long = 0,
    val goalId: Long,
    val date: LocalDate,
)
