package com.trans.transcript.dependencyinjection

import com.trans.transcript.integration.client.HttpClientService
import com.trans.transcript.integration.transacription.TranscriptionService
import com.trans.transcript.messaging.MessagingProvider
import com.trans.transcript.service.HandlerProvider
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger

val TG_SERVICE_MODULE = module {
    single { Dispatchers.IO }
    single { HttpClientService() }
    single { TranscriptionService(get()) }
    single { HandlerProvider(get()) }
    single { MessagingProvider(get()) }
}

fun Application.configureDependencies() {
    install(Koin) {
        SLF4JLogger()
        modules(TG_SERVICE_MODULE)
    }
}
