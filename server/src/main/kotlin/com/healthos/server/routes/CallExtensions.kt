package com.healthos.server.routes

import com.healthos.server.security.JwtConfig
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal

fun ApplicationCall.accountId(): String =
    principal<JWTPrincipal>()!!.payload.getClaim(JwtConfig.ACCOUNT_ID_CLAIM).asString()
