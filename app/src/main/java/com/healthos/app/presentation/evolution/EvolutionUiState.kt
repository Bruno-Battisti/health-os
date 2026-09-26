package com.healthos.app.presentation.evolution

import com.healthos.app.domain.model.WeightEntry
import com.healthos.app.domain.model.WeightTrend

data class EvolutionUiState(
    val history: List<WeightEntry> = emptyList(),
    val trend30: WeightTrend? = null,
    val isLoading: Boolean = true,
)
