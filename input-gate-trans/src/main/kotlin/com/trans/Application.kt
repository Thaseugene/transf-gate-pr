package com.trans

import com.trans.configuration.configureApplication
import com.trans.configuration.configureDatabase
import com.trans.configuration.invokeMigrationProcess
import com.trans.dependencyinjection.configureDependencies
import com.trans.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    invokeMigrationProcess(configureDatabase(configureApplication()))
    configureDependencies()
    configureSerialization()
    configureKafka()
    configureRouting()
}
