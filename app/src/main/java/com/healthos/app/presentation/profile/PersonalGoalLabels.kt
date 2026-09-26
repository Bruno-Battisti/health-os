package com.healthos.app.presentation.profile

import com.healthos.app.domain.model.PersonalGoal

fun PersonalGoal.displayLabel(): String = when (this) {
    PersonalGoal.MAINTAIN_WEIGHT -> "Manter peso"
    PersonalGoal.GAIN_MUSCLE -> "Ganhar massa"
    PersonalGoal.LOSE_WEIGHT -> "Reduzir peso"
    PersonalGoal.IMPROVE_CONDITIONING -> "Melhorar condicionamento"
    PersonalGoal.INCREASE_STRENGTH -> "Aumentar força"
    PersonalGoal.TRACK_PROGRESS -> "Acompanhar evolução"
}
