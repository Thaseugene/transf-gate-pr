package com.trans

import com.trans.configuration.*
import com.trans.dependencyinjection.configureDependencies
import com.trans.integration.tg.configureBot
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
