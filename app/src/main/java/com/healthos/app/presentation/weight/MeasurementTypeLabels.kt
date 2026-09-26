package com.healthos.app.presentation.weight

import com.healthos.app.domain.model.MeasurementType

fun MeasurementType.displayLabel(): String = when (this) {
    MeasurementType.ARM -> "Braço"
    MeasurementType.CHEST -> "Peito"
    MeasurementType.WAIST -> "Cintura"
    MeasurementType.ABDOMEN -> "Abdômen"
    MeasurementType.HIP -> "Quadril"
    MeasurementType.THIGH -> "Coxa"
    MeasurementType.CALF -> "Panturrilha"
}
