package storage.trans.com.configuration

import com.transf.kafka.messaging.service.ConsumingProvider
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureShutdownEvent() {
    val consumingProvider by inject<ConsumingProvider>()
    consumingProvider.stopConsuming()
}
