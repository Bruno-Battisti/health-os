package com.healthos.server.routes

import com.healthos.server.repository.AccountRepository
import com.healthos.shared.dto.LoginRequest
import com.healthos.shared.dto.RegisterRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.authRoutes(accountRepository: AccountRepository) {
    post("/auth/register") {
        val request = call.receive<RegisterRequest>()
        accountRepository.register(request.email, request.password).fold(
            onSuccess = { call.respond(HttpStatusCode.Created, it) },
            onFailure = { call.respond(HttpStatusCode.Conflict, mapOf("error" to it.message)) },
        )
    }

    post("/auth/login") {
        val request = call.receive<LoginRequest>()
        accountRepository.login(request.email, request.password).fold(
            onSuccess = { call.respond(HttpStatusCode.OK, it) },
            onFailure = { call.respond(HttpStatusCode.Unauthorized, mapOf("error" to it.message)) },
        )
    }
}
