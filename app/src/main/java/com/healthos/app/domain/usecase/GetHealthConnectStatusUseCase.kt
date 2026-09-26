package com.healthos.app.domain.usecase

import com.healthos.app.domain.repository.HealthConnectRepository
import javax.inject.Inject

data class HealthConnectStatus(val isAvailable: Boolean, val hasAllPermissions: Boolean, val requiredPermissions: Set<String>)

class GetHealthConnectStatusUseCase @Inject constructor(
    private val healthConnectRepository: HealthConnectRepository,
) {
    suspend operator fun invoke(): HealthConnectStatus = HealthConnectStatus(
        isAvailable = healthConnectRepository.isAvailable(),
        hasAllPermissions = healthConnectRepository.hasAllPermissions(),
        requiredPermissions = healthConnectRepository.requiredPermissions,
    )
}
