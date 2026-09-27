package com.healthos.app.data.remote

import com.healthos.shared.dto.UserProfileDto
import com.healthos.shared.dto.WeightEntryDto
import com.healthos.shared.dto.WeightSyncPullResponse
import com.healthos.shared.dto.WeightSyncPushRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import javax.inject.Inject

class SyncApi @Inject constructor(
    private val httpClient: HttpClient,
) {
    suspend fun getProfile(): UserProfileDto? {
        val response = httpClient.get("sync/profile")
        if (response.status == HttpStatusCode.NotFound) return null
        return response.bodyOrThrow()
    }

    suspend fun putProfile(profile: UserProfileDto) {
        httpClient.put("sync/profile") {
            contentType(ContentType.Application.Json)
            setBody(profile)
        }.checkSuccessOrThrow()
    }

    suspend fun pullWeightEntries(since: Long): WeightSyncPullResponse =
        httpClient.get("sync/weight-entries") {
            parameter("since", since)
        }.bodyOrThrow()

    suspend fun pushWeightEntries(entries: List<WeightEntryDto>) {
        if (entries.isEmpty()) return
        httpClient.post("sync/weight-entries") {
            contentType(ContentType.Application.Json)
            setBody(WeightSyncPushRequest(entries))
        }.checkSuccessOrThrow()
    }
}
