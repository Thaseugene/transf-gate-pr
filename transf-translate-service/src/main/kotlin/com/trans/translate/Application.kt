package com.trans.translate

import com.trans.transcript.configuration.ApplicationConfiguration
import com.trans.transcript.configuration.configureApplication
import io.ktor.server.application.*
import com.trans.translate.configuration.configureMessaging
import com.trans.translate.configuration.configureShutdownEvent
import com.trans.translate.dependencyinjection.configureDependencies

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
    val appConfiguration: ApplicationConfiguration = configureApplication()

    configureDependencies()
    configureMessaging(appConfiguration.kafkaConfig)

    environment.monitor.subscribe(ApplicationStopPreparing) {
        configureShutdownEvent()
    }
}
