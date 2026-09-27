package com.healthos.server.routes

import com.healthos.server.repository.ProfileRepository
import com.healthos.server.repository.WeightSyncRepository
import com.healthos.shared.dto.UserProfileDto
import com.healthos.shared.dto.WeightSyncPullResponse
import com.healthos.shared.dto.WeightSyncPushRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.syncRoutes(profileRepository: ProfileRepository, weightSyncRepository: WeightSyncRepository) {
    route("/sync/profile") {
        get {
            val profile = profileRepository.get(call.accountId())
            if (profile == null) call.respond(HttpStatusCode.NotFound) else call.respond(profile)
        }
        put {
            val dto = call.receive<UserProfileDto>()
            profileRepository.upsert(call.accountId(), dto)
            call.respond(HttpStatusCode.NoContent)
        }
    }

    route("/sync/weight-entries") {
        get {
            val since = call.request.queryParameters["since"]?.toLongOrNull() ?: 0L
            val entries = weightSyncRepository.listUpdatedSince(call.accountId(), since)
            call.respond(WeightSyncPullResponse(entries))
        }
        post {
            val request = call.receive<WeightSyncPushRequest>()
            weightSyncRepository.upsertAll(call.accountId(), request.entries)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
