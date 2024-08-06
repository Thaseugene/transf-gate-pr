package com.trans.dependencyinjection

import com.trans.api.EventController
import com.trans.integration.transacription.TranscriptionService
import com.trans.persistanse.EventRepository
import com.trans.persistanse.EventRepositoryImpl
import com.trans.persistanse.TestRepository
import com.trans.persistanse.TestRepositoryImpl
import com.trans.plugins.KafkaService
import com.trans.service.EventService
import com.trans.service.EventServiceImpl
import com.trans.service.TestService
import com.trans.service.TestServiceImpl
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger

val gateModule = module {
    single { Dispatchers.IO }
    single<TestRepository> { TestRepositoryImpl() }
    single<TestService> { TestServiceImpl(get()) }
    single<EventRepository> { EventRepositoryImpl() }
    single<EventService> { EventServiceImpl(get()) }
    single<TranscriptionService> { TranscriptionService(get()) }
    single<KafkaService> { KafkaService(get(), get()) }
    single<EventController> { EventController(get(), get()) }
}

fun Application.configureDependencies() {
    install(Koin) {
        SLF4JLogger()
        modules(gateModule)
    }

}
