package com.trans.dependencyinjection

import com.trans.persistanse.MessageRepository
import com.trans.persistanse.MessageRepositoryImpl
import com.trans.persistanse.UserRepository
import com.trans.persistanse.UserRepositoryImpl
import com.trans.plugins.KafkaService
import com.trans.service.EventService
import com.trans.service.EventServiceImpl
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger

val gateModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single<MessageRepository> { MessageRepositoryImpl() }
    single<EventService> { EventServiceImpl(get()) }
    single<KafkaService> { KafkaService(get()) }
}

fun Application.configureDependencies() {
    install(Koin) {
        SLF4JLogger()
        modules(gateModule)
    }

}
