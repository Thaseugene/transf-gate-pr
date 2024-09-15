package com.trans.telegram.dependencyinjection

import com.trans.telegram.configuration.BotConfiguration
import com.trans.telegram.service.tg.BotService
import com.trans.telegram.service.HandlerProviderImpl
import com.trans.telegram.service.MessageService
import com.trans.telegram.service.MessageServiceImpl
import com.trans.telegram.service.cache.CacheService
import com.trans.telegram.service.processing.ProcessingMessageHandler
import com.trans.telegram.service.tg.BotMessageProcessor
import com.transf.kafka.messaging.service.*
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger


val TG_SERVICE_MODULE = module {
    single { Dispatchers.IO }
    single<ProducingProvider> { ProducingProviderImpl() }
    single { CacheService() }
    single<MessageService> { MessageServiceImpl(get(), get(), get()) }
    single { BotMessageProcessor(get(), get(), BotConfiguration.TG_BOT) }
    single { BotService(get(), BotConfiguration.TG_BOT) }
    single(named("handlers")) { prepareHandlers() }
    single<HandlerProvider> { HandlerProviderImpl(get(named("handlers"))) }
    single<ConsumingProvider> { ConsumingProviderImpl(get(), get()) }
}

fun Application.configureDependencies() {
    install(Koin) {
        SLF4JLogger()
        modules(TG_SERVICE_MODULE)
    }
}

private fun Scope.prepareHandlers() = listOf(
    ProcessingMessageHandler(get(), get()),
)
