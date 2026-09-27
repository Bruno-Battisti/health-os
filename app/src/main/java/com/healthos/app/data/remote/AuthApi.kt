package com.healthos.app.data.remote

import com.healthos.shared.dto.AuthResponse
import com.healthos.shared.dto.LoginRequest
import com.healthos.shared.dto.RegisterRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class AuthApi @Inject constructor(
    private val httpClient: HttpClient,
) {
    suspend fun register(email: String, password: String): AuthResponse =
        httpClient.post("auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(email, password))
        }.bodyOrThrow()

    suspend fun login(email: String, password: String): AuthResponse =
        httpClient.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }.bodyOrThrow()
}
