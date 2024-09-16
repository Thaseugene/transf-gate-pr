package com.trans.transcript.dependencyinjection

import com.trans.transcript.service.HandlerProviderImpl
import com.trans.transcript.service.MessageService
import com.trans.transcript.service.MessageServiceImpl
import com.trans.transcript.service.integration.client.HttpClientService
import com.trans.transcript.service.integration.transacription.TranscriptionService
import com.trans.transcript.service.integration.transacription.TranscriptionServiceImpl
import com.transf.kafka.messaging.service.*
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger

val TG_SERVICE_MODULE = module {
    single { Dispatchers.IO }
    single { HttpClientService() }
    single { TranscriptionServiceImpl(get()) as TranscriptionService }
    single { ProducingProviderImpl() as ProducingProvider }
    single { MessageServiceImpl(get(), get()) as MessageService }
    single { HandlerProviderImpl(get(), get(), get()) as HandlerProvider }
    single { ConsumingProviderImpl(get(), get()) as ConsumingProvider }
}

fun Application.configureDependencies() {
    install(Koin) {
        SLF4JLogger()
        modules(TG_SERVICE_MODULE)
    }
}
