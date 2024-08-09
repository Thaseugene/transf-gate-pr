package com.trans.dependencyinjection

import com.trans.api.EventController
import com.trans.persistanse.MessageRepository
import com.trans.persistanse.MessageRepositoryImpl
import com.trans.persistanse.TestRepository
import com.trans.persistanse.TestRepositoryImpl
import com.trans.plugins.KafkaService
import com.trans.service.EventService
import com.trans.service.EventServiceImpl
//import com.trans.service.StreamingService
import com.trans.service.TestService
import com.trans.service.TestServiceImpl
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger

val gateModule = module {
    single<TestRepository> { TestRepositoryImpl() }
    single<TestService> { TestServiceImpl(get()) }
    single<MessageRepository> { MessageRepositoryImpl() }
    single<EventService> { EventServiceImpl(get()) }
    single<KafkaService> { KafkaService(get()) }
    single<EventController> { EventController(get(), get()) }
}

fun Application.configureDependencies() {
    install(Koin) {
        SLF4JLogger()
        modules(gateModule)
    }

}
