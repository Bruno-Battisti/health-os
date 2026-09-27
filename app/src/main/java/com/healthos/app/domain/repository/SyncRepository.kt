package com.healthos.app.domain.repository

import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface SyncRepository {
    suspend fun syncNow(): Result<Unit>
    fun observeLastSyncedAt(): Flow<Instant?>
}
