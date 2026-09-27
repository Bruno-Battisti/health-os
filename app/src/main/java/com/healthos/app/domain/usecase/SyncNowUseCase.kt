package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.SyncRepository
import javax.inject.Inject

class SyncNowUseCase @Inject constructor(
    private val syncRepository: SyncRepository,
) {
    suspend operator fun invoke(): Result<Unit> = syncRepository.syncNow()
}
