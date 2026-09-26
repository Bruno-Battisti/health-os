package com.healthos.app.domain.model

import java.time.Instant
import java.time.LocalDate

data class Goal(
    val id: Long = 0,
    val userId: Long,
    val description: String,
    val targetCount: Int,
    val deadline: LocalDate,
    val createdAt: Instant,
)
