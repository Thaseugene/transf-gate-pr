package com.trans.telegram

import com.trans.telegram.configuration.*
import com.trans.telegram.dependencyinjection.configureDependencies
import com.trans.telegram.integration.tg.configureBot
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
    configureDependencies()
    configureApplication().also {
        configureCache(it)
        configureMessaging(it)
    }
    configureBot()

    environment.monitor.subscribe(ApplicationStopPreparing) {
        configureShutdownEvent()
    }

}
