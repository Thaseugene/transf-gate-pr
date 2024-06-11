package com.trans.dependencyinjection

import com.trans.api.TestController
import com.trans.persistanse.TestRepository
import com.trans.persistanse.TestRepositoryImpl
import com.trans.service.StreamingService
import com.trans.service.TestService
import com.trans.service.TestServiceImpl
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger

val gateModule = module {
    single<TestRepository> { TestRepositoryImpl() }
    single<TestService> { TestServiceImpl(get()) }
    single<TestController> { TestController(get()) }
    single<StreamingService> { StreamingService() }
}

fun Application.configureDependencies() {
    install(Koin) {
        SLF4JLogger()
        modules(gateModule)
    }

}
