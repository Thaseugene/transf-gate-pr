package com.trans.telegram

import com.trans.telegram.configuration.*
import com.trans.telegram.dependencyinjection.configureDependencies
import io.ktor.server.application.*
import io.ktor.server.cio.*

fun main(args: Array<String>) {
    EngineMain.main(args)
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
