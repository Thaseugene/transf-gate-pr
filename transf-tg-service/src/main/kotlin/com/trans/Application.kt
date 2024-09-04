package com.trans

import com.trans.configuration.ApplicationConfiguration
import com.trans.configuration.configureApplication
import com.trans.configuration.configureMessaging
import com.trans.dependencyinjection.configureDependencies
import com.trans.integration.tg.configureBot
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {

    val appConfiguration: ApplicationConfiguration = configureApplication()

    configureDependencies()
    configureMessaging(appConfiguration.kafkaConfig)
    configureBot()

}
