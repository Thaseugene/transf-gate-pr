package com.trans.dependencyinjection

import com.trans.integration.tg.BotService
import com.trans.integration.transacription.TranscriptionService
import com.trans.persistanse.MessageRepository
import com.trans.persistanse.MessageRepositoryImpl
import com.trans.persistanse.UserRepository
import com.trans.persistanse.UserRepositoryImpl
//import com.trans.plugins.KafkaService
import com.trans.service.MessageService
import com.trans.service.MessageServiceImpl
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger

val gateModule = module {
    single { Dispatchers.IO }
    single<UserRepository> { UserRepositoryImpl() }
    single<MessageRepository> { MessageRepositoryImpl() }
    single<MessageService> { MessageServiceImpl(get(), get()) }
    single<TranscriptionService> { TranscriptionService(get()) }
//    single<KafkaService> { KafkaService(get(), get()) }
    single<BotService> { BotService(get(), get(), get()) }
}

fun Application.configureDependencies() {
    install(Koin) {
        SLF4JLogger()
        modules(gateModule)
    }

}
