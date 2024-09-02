package com.trans.transcript.dependencyinjection

import com.trans.transcript.service.HandlerProvider
import com.trans.transcript.integration.transacription.TranscriptionService
import com.trans.transcript.messaging.MessagingProvider
import io.ktor.server.application.*

import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger

val TG_SERVICE_MODULE = module {
    single { Dispatchers.IO }
    single { TranscriptionService(lazy { get() }) }
    single { HandlerProvider(get(), lazy { get() }, lazy { get() }) }
    single { MessagingProvider(get(), lazy { get() }) }
}

fun Application.configureDependencies() {
    install(Koin) {
        SLF4JLogger()
        modules(TG_SERVICE_MODULE)
    }
}
