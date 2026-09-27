package com.healthos.server.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import java.util.Date

private const val THIRTY_DAYS_MILLIS = 30L * 24 * 60 * 60 * 1000

object JwtConfig {
    const val ISSUER = "healthos-server"
    const val AUDIENCE = "healthos-app"
    const val ACCOUNT_ID_CLAIM = "accountId"
    const val AUTH_CONFIG_NAME = "auth-jwt"

    private val secret = System.getenv("JWT_SECRET") ?: "dev-secret-change-me"
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .build()

    fun generateToken(accountId: String): String = JWT.create()
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .withClaim(ACCOUNT_ID_CLAIM, accountId)
        .withExpiresAt(Date(System.currentTimeMillis() + THIRTY_DAYS_MILLIS))
        .sign(algorithm)
}
