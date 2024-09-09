package translate.transf.com.configuration

import com.transf.kafka.messaging.service.ConsumingProvider
import com.transf.kafka.messaging.configuration.KafkaInnerConfig
import com.transf.kafka.messaging.service.ProducingProvider
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject

fun Application.configureMessaging(kafkaConfig: KafkaInnerConfig) {
    val producingProvider by inject<ProducingProvider>()
    val consumingProvider by inject<ConsumingProvider>()
    val dispatcher by inject<CoroutineDispatcher>()

    CoroutineScope(dispatcher).launch {
        consumingProvider.prepareConsumerMessaging(kafkaConfig)
        producingProvider.prepareProducerMessaging(kafkaConfig)
    }
}
