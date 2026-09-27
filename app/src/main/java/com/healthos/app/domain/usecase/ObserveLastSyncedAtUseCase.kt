package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.SyncRepository
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject

class ObserveLastSyncedAtUseCase @Inject constructor(
    private val syncRepository: SyncRepository,
) {
    operator fun invoke(): Flow<Instant?> = syncRepository.observeLastSyncedAt()
}
