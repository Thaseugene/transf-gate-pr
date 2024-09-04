package com.trans.configuration

import com.transf.kafka.messaging.MessagingProvider
import com.transf.kafka.messaging.configuration.KafkaInnerConfig
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject

fun Application.configureMessaging(kafkaConfig: KafkaInnerConfig) {
    val messagingProvider by inject<MessagingProvider>()
    val dispatcher by inject<CoroutineDispatcher>()

    CoroutineScope(dispatcher).launch {
        messagingProvider.prepareConsumerMessaging(kafkaConfig)
        messagingProvider.prepareProducerMessaging(kafkaConfig)
    }
}
