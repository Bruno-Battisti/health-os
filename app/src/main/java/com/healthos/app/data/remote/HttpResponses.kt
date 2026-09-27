package com.healthos.app.data.remote

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

suspend inline fun <reified T> HttpResponse.bodyOrThrow(): T {
    if (status.isSuccess()) return body()
    throw ApiException(extractError())
}

suspend fun HttpResponse.checkSuccessOrThrow() {
    if (!status.isSuccess()) throw ApiException(extractError())
}

suspend fun HttpResponse.extractError(): String =
    runCatching { body<Map<String, String?>>()["error"] }.getOrNull() ?: "Erro inesperado (${status.value})"
