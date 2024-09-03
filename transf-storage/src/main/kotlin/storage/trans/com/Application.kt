package storage.trans.com

import com.trans.dependencyinjection.configureDependencies
import io.ktor.server.application.*
import storage.trans.com.configuration.configureApplication
import storage.trans.com.configuration.configureDatabase
import storage.trans.com.configuration.invokeMigrationProcess
import storage.trans.com.messaging.configureMessaging

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {

    val appConfiguration = configureApplication()

    invokeMigrationProcess(configureDatabase(appConfiguration))
    configureDependencies()
    configureMessaging(appConfiguration.kafkaConfig)
//    configureSerialization()


}
