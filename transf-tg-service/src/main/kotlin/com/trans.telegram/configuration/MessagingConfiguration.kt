package com.trans.telegram.configuration

import com.transf.kafka.messaging.service.ConsumingProvider
import com.transf.kafka.messaging.service.ProducingProvider
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject

fun Application.configureMessaging(appConfig: ApplicationConfiguration) {
    val producingProvider by inject<ProducingProvider>()
    val consumingProvider by inject<ConsumingProvider>()
    val dispatcher by inject<CoroutineDispatcher>()

    CoroutineScope(dispatcher).launch {
        consumingProvider.prepareConsumerMessaging(appConfig.kafkaConfig)
        producingProvider.prepareProducerMessaging(appConfig.kafkaConfig)
    }
}
