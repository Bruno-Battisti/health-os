package com.healthos.server

import com.healthos.server.database.DatabaseFactory
import com.healthos.server.plugins.configureAuthentication
import com.healthos.server.plugins.configureRouting
import com.healthos.server.plugins.configureSerialization
import com.healthos.server.plugins.configureStatusPages
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, module = Application::module).start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureAuthentication()
    configureStatusPages()
    configureRouting()
}
