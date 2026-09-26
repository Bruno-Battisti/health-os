package com.healthos.app.presentation.weight

import com.healthos.app.domain.model.Measurement
import com.healthos.app.domain.model.MeasurementType

data class MeasurementUiState(
    val selectedType: MeasurementType = MeasurementType.ARM,
    val valueInput: String = "",
    val errorMessage: String? = null,
    val history: List<Measurement> = emptyList(),
)
