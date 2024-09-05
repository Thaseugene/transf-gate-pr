package com.trans.dependencyinjection

import com.trans.configuration.ApplicationConfiguration
import com.trans.integration.tg.BotService
import com.trans.service.HandlerProviderImpl
import com.trans.service.MessageService
import com.trans.service.MessageServiceImpl
import com.trans.service.cache.CacheService
import com.transf.kafka.messaging.service.*
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger


val TG_SERVICE_MODULE = module {
    single { Dispatchers.IO }
    single { ProducingProviderImpl() as ProducingProvider }
    single { CacheService() }
    single { MessageServiceImpl(get(), get(), get()) as MessageService }
    single { BotService(get(), get()) }
    single { HandlerProviderImpl(get(), get()) as HandlerProvider }
    single { ConsumingProviderImpl(get(), get()) as ConsumingProvider }
}

fun Application.configureDependencies() {
    install(Koin) {
        SLF4JLogger()
        modules(TG_SERVICE_MODULE)
    }
}
