package com.healthos.app.domain.model

import java.time.Instant
import java.time.LocalDate

data class User(
    val name: String,
    val birthDate: LocalDate,
    val heightCm: Float,
    val createdAt: Instant,
) {
    companion object {
        const val SINGLE_USER_ID = 1L
    }
}
