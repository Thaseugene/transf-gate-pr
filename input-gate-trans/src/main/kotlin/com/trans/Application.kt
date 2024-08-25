package com.trans

import com.trans.configuration.ApplicationConfiguration
import com.trans.configuration.configureApplication
import com.trans.dependencyinjection.configureDependencies
import com.trans.integration.tg.configureBot
import com.trans.plugins.configureSerialization
import io.ktor.server.application.*
import storage.trans.com.messaging.configureMessaging

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val appConfiguration: ApplicationConfiguration = configureApplication()

    configureDependencies()
    configureSerialization()
    configureMessaging(appConfiguration.kafkaConfig)
    configureBot()
}
