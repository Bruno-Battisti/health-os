package com.healthos.server.plugins

import com.healthos.server.repository.AccountRepository
import com.healthos.server.repository.ProfileRepository
import com.healthos.server.repository.WeightSyncRepository
import com.healthos.server.routes.authRoutes
import com.healthos.server.routes.syncRoutes
import com.healthos.server.security.JwtConfig
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    val accountRepository = AccountRepository()
    val profileRepository = ProfileRepository()
    val weightSyncRepository = WeightSyncRepository()

    routing {
        authRoutes(accountRepository)
        authenticate(JwtConfig.AUTH_CONFIG_NAME) {
            syncRoutes(profileRepository, weightSyncRepository)
        }
    }
}
