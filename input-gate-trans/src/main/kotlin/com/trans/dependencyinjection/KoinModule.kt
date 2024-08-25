package com.trans.dependencyinjection

import com.trans.integration.tg.BotService
import com.trans.service.HandlerProvider
import com.trans.service.MessageService
import com.trans.service.MessageServiceImpl
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger
import storage.trans.com.messaging.MessagingProvider

val TG_SERVICE_MODULE = module {
    single { Dispatchers.IO }
    single { BotService(get()) }
    single { MessageServiceImpl(get()) as MessageService }
    single { MessagingProvider(get(), lazy { get() }) }
    single { HandlerProvider(get(), lazy { get() }) }
}

fun Application.configureDependencies() {
    install(Koin) {
        SLF4JLogger()
        modules(TG_SERVICE_MODULE)
    }

}
