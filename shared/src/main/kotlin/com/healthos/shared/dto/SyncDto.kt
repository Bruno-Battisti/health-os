package com.healthos.shared.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val name: String,
    val birthDateEpochDay: Long,
    val heightCm: Float,
    val createdAtEpochMilli: Long,
    val updatedAtEpochMilli: Long,
)

@Serializable
data class WeightEntryDto(
    val remoteId: String,
    val weight: Float,
    val dateEpochDay: Long,
    val note: String?,
    val updatedAtEpochMilli: Long,
)

@Serializable
data class WeightSyncPushRequest(val entries: List<WeightEntryDto>)

@Serializable
data class WeightSyncPullResponse(val entries: List<WeightEntryDto>)
