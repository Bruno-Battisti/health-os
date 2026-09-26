package com.healthos.app.domain.model

data class WeightTrend(
    val currentWeight: Float,
    val referenceWeight: Float?,
    val periodDays: Int,
) {
    val deltaKg: Float? = referenceWeight?.let { currentWeight - it }
}
