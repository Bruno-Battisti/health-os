package com.healthos.server.plugins

import com.healthos.server.security.JwtConfig
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureAuthentication() {
    install(Authentication) {
        jwt(JwtConfig.AUTH_CONFIG_NAME) {
            verifier(JwtConfig.verifier)
            validate { credential ->
                val accountId = credential.payload.getClaim(JwtConfig.ACCOUNT_ID_CLAIM).asString()
                if (accountId != null) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
