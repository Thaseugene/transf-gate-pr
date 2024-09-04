package com.trans.dependencyinjection

import com.trans.integration.tg.BotService
import com.trans.service.HandlerProviderImpl
import com.trans.service.MessageService
import com.trans.service.MessageServiceImpl
import com.transf.kafka.messaging.MessagingProvider
import com.transf.kafka.messaging.MessagingProviderImpl
import com.transf.kafka.messaging.service.HandlerProvider
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger


val TG_SERVICE_MODULE = module {
    single { Dispatchers.IO }
    single { BotService() }
    single { MessageServiceImpl() as MessageService }
    single { HandlerProviderImpl() as HandlerProvider }
    single { MessagingProviderImpl(get(), get()) as MessagingProvider}
}

fun Application.configureDependencies() {
    install(Koin) {
        SLF4JLogger()
        modules(TG_SERVICE_MODULE)
    }
}
