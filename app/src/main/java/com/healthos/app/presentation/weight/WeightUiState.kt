package com.healthos.app.presentation.weight

import com.healthos.app.domain.model.WeightEntry
import com.healthos.app.domain.model.WeightTrend

data class WeightUiState(
    val weightInput: String = "",
    val noteInput: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val history: List<WeightEntry> = emptyList(),
    val trend7: WeightTrend? = null,
    val trend30: WeightTrend? = null,
    val trend90: WeightTrend? = null,
)
