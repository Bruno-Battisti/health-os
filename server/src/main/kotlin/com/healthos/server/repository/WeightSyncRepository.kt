package com.healthos.server.repository

import com.healthos.server.database.WeightEntries
import com.healthos.shared.dto.WeightEntryDto
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.v1.jdbc.update

class WeightSyncRepository {

    suspend fun listUpdatedSince(accountId: String, since: Long): List<WeightEntryDto> = newSuspendedTransaction {
        WeightEntries.selectAll()
            .where { (WeightEntries.accountId eq accountId) and (WeightEntries.updatedAt greater since) }
            .map { it.toDto() }
    }

    suspend fun upsertAll(accountId: String, entries: List<WeightEntryDto>) = newSuspendedTransaction {
        entries.forEach { dto ->
            val existing = WeightEntries.selectAll().where { WeightEntries.remoteId eq dto.remoteId }.firstOrNull()

            when {
                existing == null -> WeightEntries.insert {
                    it[remoteId] = dto.remoteId
                    it[WeightEntries.accountId] = accountId
                    it[weight] = dto.weight
                    it[dateEpochDay] = dto.dateEpochDay
                    it[note] = dto.note
                    it[updatedAt] = dto.updatedAtEpochMilli
                }
                existing[WeightEntries.accountId] == accountId -> WeightEntries.update(
                    { (WeightEntries.remoteId eq dto.remoteId) and (WeightEntries.accountId eq accountId) },
                ) {
                    it[weight] = dto.weight
                    it[dateEpochDay] = dto.dateEpochDay
                    it[note] = dto.note
                    it[updatedAt] = dto.updatedAtEpochMilli
                }
                else -> Unit // entry belongs to another account; ignore to avoid cross-account overwrite
            }
        }
    }

    private fun ResultRow.toDto() = WeightEntryDto(
        remoteId = this[WeightEntries.remoteId],
        weight = this[WeightEntries.weight],
        dateEpochDay = this[WeightEntries.dateEpochDay],
        note = this[WeightEntries.note],
        updatedAtEpochMilli = this[WeightEntries.updatedAt],
    )
}
