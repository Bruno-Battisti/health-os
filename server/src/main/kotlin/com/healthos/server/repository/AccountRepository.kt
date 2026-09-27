package com.healthos.server.repository

import com.healthos.server.database.Accounts
import com.healthos.server.security.JwtConfig
import com.healthos.shared.dto.AuthResponse
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

class AccountRepository {

    suspend fun register(email: String, password: String): Result<AuthResponse> = newSuspendedTransaction {
        val existing = Accounts.selectAll().where { Accounts.email eq email }.firstOrNull()
        if (existing != null) {
            return@newSuspendedTransaction Result.failure(IllegalStateException("E-mail já cadastrado"))
        }

        val id = UUID.randomUUID().toString()
        Accounts.insert {
            it[Accounts.id] = id
            it[Accounts.email] = email
            it[passwordHash] = BCrypt.hashpw(password, BCrypt.gensalt())
            it[createdAt] = System.currentTimeMillis()
        }

        Result.success(AuthResponse(token = JwtConfig.generateToken(id), accountId = id))
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> = newSuspendedTransaction {
        val row = Accounts.selectAll().where { Accounts.email eq email }.firstOrNull()
            ?: return@newSuspendedTransaction Result.failure(IllegalStateException("Credenciais inválidas"))

        if (!BCrypt.checkpw(password, row[Accounts.passwordHash])) {
            return@newSuspendedTransaction Result.failure(IllegalStateException("Credenciais inválidas"))
        }

        val id = row[Accounts.id]
        Result.success(AuthResponse(token = JwtConfig.generateToken(id), accountId = id))
    }
}
