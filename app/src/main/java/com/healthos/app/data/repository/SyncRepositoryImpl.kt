package com.healthos.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.healthos.app.data.local.dao.WeightDao
import com.healthos.app.data.local.entity.WeightEntryEntity
import com.healthos.app.data.remote.SyncApi
import com.healthos.app.domain.model.User
import com.healthos.app.domain.repository.AuthRepository
import com.healthos.app.domain.repository.SyncRepository
import com.healthos.app.domain.repository.UserRepository
import com.healthos.shared.dto.UserProfileDto
import com.healthos.shared.dto.WeightEntryDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

private object SyncPreferencesKeys {
    val WEIGHT_LAST_PUSHED_AT = longPreferencesKey("sync_weight_last_pushed_at")
    val WEIGHT_LAST_PULLED_AT = longPreferencesKey("sync_weight_last_pulled_at")
    val LAST_SYNCED_AT = longPreferencesKey("sync_last_synced_at")
}

class SyncRepositoryImpl @Inject constructor(
    private val syncApi: SyncApi,
    private val weightDao: WeightDao,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val dataStore: DataStore<Preferences>,
) : SyncRepository {

    override fun observeLastSyncedAt(): Flow<Instant?> =
        dataStore.data.map { preferences -> preferences[SyncPreferencesKeys.LAST_SYNCED_AT]?.let(Instant::ofEpochMilli) }

    override suspend fun syncNow(): Result<Unit> {
        if (authRepository.session.value == null) {
            return Result.failure(IllegalStateException("Faça login para sincronizar"))
        }

        return runCatching {
            syncProfile()
            syncWeightEntries()
            dataStore.edit { it[SyncPreferencesKeys.LAST_SYNCED_AT] = System.currentTimeMillis() }
        }
    }

    private suspend fun syncProfile() {
        val localUser = userRepository.observeUser().first()
        val remoteProfile = syncApi.getProfile()

        when {
            remoteProfile == null && localUser != null -> syncApi.putProfile(localUser.toDto())
            remoteProfile != null && localUser == null -> userRepository.saveUser(remoteProfile.toDomain())
            remoteProfile != null && localUser != null -> {
                val localUpdatedAt = localUser.updatedAt.toEpochMilli()
                when {
                    localUpdatedAt > remoteProfile.updatedAtEpochMilli -> syncApi.putProfile(localUser.toDto())
                    remoteProfile.updatedAtEpochMilli > localUpdatedAt -> userRepository.saveUser(remoteProfile.toDomain())
                }
            }
        }
    }

    private suspend fun syncWeightEntries() {
        val pushCursor = System.currentTimeMillis()
        val lastPushedAt = dataStore.data.first()[SyncPreferencesKeys.WEIGHT_LAST_PUSHED_AT] ?: 0L
        val dirty = weightDao.getUpdatedSince(User.SINGLE_USER_ID, lastPushedAt).map { entry ->
            entry.remoteId?.let { entry } ?: run {
                val remoteId = UUID.randomUUID().toString()
                weightDao.assignRemoteId(entry.id, remoteId)
                entry.copy(remoteId = remoteId)
            }
        }
        syncApi.pushWeightEntries(dirty.map { it.toDto() })
        dataStore.edit { it[SyncPreferencesKeys.WEIGHT_LAST_PUSHED_AT] = pushCursor }

        val pullCursor = System.currentTimeMillis()
        val lastPulledAt = dataStore.data.first()[SyncPreferencesKeys.WEIGHT_LAST_PULLED_AT] ?: 0L
        syncApi.pullWeightEntries(lastPulledAt).entries.forEach { dto ->
            val existing = weightDao.findByRemoteId(dto.remoteId)
            if (existing != null) {
                weightDao.update(
                    existing.copy(weight = dto.weight, date = dto.dateEpochDay, note = dto.note, updatedAt = dto.updatedAtEpochMilli),
                )
            } else {
                weightDao.insert(
                    WeightEntryEntity(
                        userId = User.SINGLE_USER_ID,
                        weight = dto.weight,
                        date = dto.dateEpochDay,
                        note = dto.note,
                        remoteId = dto.remoteId,
                        updatedAt = dto.updatedAtEpochMilli,
                    ),
                )
            }
        }
        dataStore.edit { it[SyncPreferencesKeys.WEIGHT_LAST_PULLED_AT] = pullCursor }
    }
}

private fun User.toDto() = UserProfileDto(
    name = name,
    birthDateEpochDay = birthDate.toEpochDay(),
    heightCm = heightCm,
    createdAtEpochMilli = createdAt.toEpochMilli(),
    updatedAtEpochMilli = updatedAt.toEpochMilli(),
)

private fun UserProfileDto.toDomain(): User = User(
    name = name,
    birthDate = LocalDate.ofEpochDay(birthDateEpochDay),
    heightCm = heightCm,
    createdAt = Instant.ofEpochMilli(createdAtEpochMilli),
    updatedAt = Instant.ofEpochMilli(updatedAtEpochMilli),
)

private fun WeightEntryEntity.toDto() = WeightEntryDto(
    remoteId = requireNotNull(remoteId),
    weight = weight,
    dateEpochDay = date,
    note = note,
    updatedAtEpochMilli = updatedAt,
)
