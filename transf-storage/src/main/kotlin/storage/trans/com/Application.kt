package storage.trans.com

import io.ktor.server.application.*
import storage.trans.com.configuration.configureApplication
import storage.trans.com.configuration.configureDatabase
import storage.trans.com.configuration.invokeMigrationProcess
import storage.trans.com.plugins.configureSerialization

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val appConfiguration = configureApplication()

    invokeMigrationProcess(configureDatabase(appConfiguration))
    configureSerialization()


}
