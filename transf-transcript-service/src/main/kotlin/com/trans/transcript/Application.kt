package com.trans.transcript

import com.trans.configuration.ApplicationConfiguration
import com.trans.configuration.configureApplication
import com.trans.transcript.dependencyinjection.configureDependencies
import com.trans.transcript.messaging.configureMessaging
import com.trans.transcript.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
    val appConfiguration: ApplicationConfiguration = configureApplication()

    configureSerialization()
    configureDependencies()
    configureMessaging(appConfiguration.kafkaConfig)
}
