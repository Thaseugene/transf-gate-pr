package com.trans.transcript

import com.trans.transcript.configuration.ApplicationConfiguration
import com.trans.transcript.configuration.configureApplication
import com.trans.transcript.configuration.configureMessaging
import com.trans.transcript.configuration.configureShutdownEvent
import com.trans.transcript.dependencyinjection.configureDependencies
import io.ktor.server.application.*

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
