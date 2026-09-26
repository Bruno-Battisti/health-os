package com.healthos.app.data.repository

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.healthos.app.domain.repository.HealthConnectRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class HealthConnectRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : HealthConnectRepository {

    override val requiredPermissions: Set<String> = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
    )

    private val client: HealthConnectClient? by lazy {
        if (HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE) {
            HealthConnectClient.getOrCreate(context)
        } else {
            null
        }
    }

    override fun isAvailable(): Boolean = client != null

    override suspend fun hasAllPermissions(): Boolean {
        val healthConnectClient = client ?: return false
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        return requiredPermissions.all { it in granted }
    }

    override suspend fun readStepsToday(): Long {
        val healthConnectClient = client ?: return 0
        val startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startOfDay, Instant.now()),
            ),
        )
        return response[StepsRecord.COUNT_TOTAL] ?: 0
    }

    override suspend fun readSleepMinutesLastNight(): Long? {
        val healthConnectClient = client ?: return null
        val now = Instant.now()
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(now.minus(Duration.ofHours(24)), now),
            ),
        )
        val latest = response.records.maxByOrNull { it.endTime } ?: return null
        return Duration.between(latest.startTime, latest.endTime).toMinutes()
    }

    override suspend fun readExerciseSessionCount(sinceDays: Int): Int {
        val healthConnectClient = client ?: return 0
        val now = Instant.now()
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = ExerciseSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(now.minus(Duration.ofDays(sinceDays.toLong())), now),
            ),
        )
        return response.records.size
    }
}
