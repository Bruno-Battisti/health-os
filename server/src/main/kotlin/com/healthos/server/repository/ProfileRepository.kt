package com.healthos.server.repository

import com.healthos.server.database.UserProfiles
import com.healthos.shared.dto.UserProfileDto
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.v1.jdbc.update

class ProfileRepository {

    suspend fun get(accountId: String): UserProfileDto? = newSuspendedTransaction {
        UserProfiles.selectAll().where { UserProfiles.accountId eq accountId }.firstOrNull()?.let { row ->
            UserProfileDto(
                name = row[UserProfiles.name],
                birthDateEpochDay = row[UserProfiles.birthDateEpochDay],
                heightCm = row[UserProfiles.heightCm],
                createdAtEpochMilli = row[UserProfiles.createdAt],
                updatedAtEpochMilli = row[UserProfiles.updatedAt],
            )
        }
    }

    suspend fun upsert(accountId: String, dto: UserProfileDto) = newSuspendedTransaction {
        val exists = UserProfiles.selectAll().where { UserProfiles.accountId eq accountId }.any()

        if (exists) {
            UserProfiles.update({ UserProfiles.accountId eq accountId }) {
                it[name] = dto.name
                it[birthDateEpochDay] = dto.birthDateEpochDay
                it[heightCm] = dto.heightCm
                it[updatedAt] = dto.updatedAtEpochMilli
            }
        } else {
            UserProfiles.insert {
                it[UserProfiles.accountId] = accountId
                it[name] = dto.name
                it[birthDateEpochDay] = dto.birthDateEpochDay
                it[heightCm] = dto.heightCm
                it[createdAt] = dto.createdAtEpochMilli
                it[updatedAt] = dto.updatedAtEpochMilli
            }
        }
    }
}
