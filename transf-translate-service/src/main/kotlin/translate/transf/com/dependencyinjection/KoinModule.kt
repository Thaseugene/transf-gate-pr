package translate.transf.com.dependencyinjection

import com.transf.kafka.messaging.service.*
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger
import translate.transf.com.integration.client.HttpClientService
import translate.transf.com.integration.translate.TranslateService
import translate.transf.com.integration.translate.TranslateServiceImpl
import translate.transf.com.service.HandlerProviderImpl
import translate.transf.com.service.MessageService
import translate.transf.com.service.MessageServiceImpl

val TG_SERVICE_MODULE = module {
    single { Dispatchers.IO }
    single { HttpClientService() }
    single { TranslateServiceImpl(get()) as TranslateService }
    single { ProducingProviderImpl() as ProducingProvider }
    single { MessageServiceImpl(get(), get()) as MessageService }
    single { HandlerProviderImpl(get(), get()) as HandlerProvider }
    single { ConsumingProviderImpl(get(), get()) as ConsumingProvider }
}

fun Application.configureDependencies() {
    install(Koin) {
        SLF4JLogger()
        modules(TG_SERVICE_MODULE)
    }
}
