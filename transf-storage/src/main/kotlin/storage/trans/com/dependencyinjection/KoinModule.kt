package com.trans.dependencyinjection


import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger
import storage.trans.com.messaging.MessagingProvider
import storage.trans.com.persistance.MessageRepository
import storage.trans.com.persistance.MessageRepositoryImpl
import storage.trans.com.persistance.UserRepository
import storage.trans.com.persistance.UserRepositoryImpl
import storage.trans.com.service.HandlerProvider
import storage.trans.com.service.MessageService
import storage.trans.com.service.MessageServiceImpl

val storageService = module {

    single { Dispatchers.IO }
    single<UserRepository> { UserRepositoryImpl() }
    single<MessageRepository> { MessageRepositoryImpl() }
    single<MessageService> { MessageServiceImpl(get(), get()) }
    single<HandlerProvider> { HandlerProvider(get()) }
    single<MessagingProvider> { MessagingProvider(get(), get()) }

}

fun Application.configureDependencies() {
    install(Koin) {
        SLF4JLogger()
        modules(storageService)
    }

}
