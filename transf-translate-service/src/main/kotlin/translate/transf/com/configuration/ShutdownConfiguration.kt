package translate.transf.com.configuration

import com.transf.kafka.messaging.service.ConsumingProvider
import com.transf.kafka.messaging.service.ProducingProvider
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureShutdownEvent() {
    val consumingProvider by inject<ConsumingProvider>()
    val producingProvider by inject<ProducingProvider>()

    consumingProvider.onShutdown()
    producingProvider.onShutdown()
}
