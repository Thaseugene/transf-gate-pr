package com.trans.dependencyinjection


import com.transf.kafka.messaging.service.*
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger
import storage.trans.com.persistance.MessageRepository
import storage.trans.com.persistance.MessageRepositoryImpl
import storage.trans.com.persistance.UserRepository
import storage.trans.com.persistance.UserRepositoryImpl
import storage.trans.com.service.HandlerProviderImpl
import storage.trans.com.service.MessageService
import storage.trans.com.service.MessageServiceImpl
import storage.trans.com.service.processing.TelegramMessageHandler
import storage.trans.com.service.processing.TranscriptMessageHandler
import storage.trans.com.service.processing.TranslateMessageHandler

val storageService = module {

    single { Dispatchers.IO }
    single<ProducingProvider> { ProducingProviderImpl() }
    single<UserRepository> { UserRepositoryImpl() }
    single<MessageRepository> { MessageRepositoryImpl() }
    single<MessageService> { MessageServiceImpl(get(), get(), get()) }
    single(named("handlers")) { prepareHandlers() }
    single<HandlerProvider> { HandlerProviderImpl(get(named("handlers"))) }
    single<ConsumingProvider> { ConsumingProviderImpl(get(), get()) }

}

fun Application.configureDependencies() {
    install(Koin) {
        SLF4JLogger()
        modules(storageService)
    }
}

private fun Scope.prepareHandlers() = listOf(
    TelegramMessageHandler(get()),
    TranscriptMessageHandler(get()),
    TranslateMessageHandler(get()),
)
