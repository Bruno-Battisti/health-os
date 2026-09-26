package com.healthos.app.domain.model

import java.time.LocalDate

data class WeightEntry(
    val id: Long = 0,
    val userId: Long,
    val weight: Float,
    val date: LocalDate,
    val note: String? = null,
)
