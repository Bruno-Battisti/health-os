package com.healthos.app.domain.repository

interface HealthConnectRepository {
    val requiredPermissions: Set<String>

    fun isAvailable(): Boolean
    suspend fun hasAllPermissions(): Boolean
    suspend fun readStepsToday(): Long
    suspend fun readSleepMinutesLastNight(): Long?
    suspend fun readExerciseSessionCount(sinceDays: Int): Int
}
