package com.healthos.app.domain.model

import java.time.LocalDate

data class Measurement(
    val id: Long = 0,
    val userId: Long,
    val type: MeasurementType,
    val value: Float,
    val date: LocalDate,
)
