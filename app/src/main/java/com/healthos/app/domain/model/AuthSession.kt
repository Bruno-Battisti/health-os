package com.healthos.app.domain.model

data class AuthSession(
    val token: String,
    val accountId: String,
)
