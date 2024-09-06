package com.trans.configuration

import com.trans.service.cache.CacheService
import com.transf.kafka.messaging.service.ConsumingProvider
import com.transf.kafka.messaging.service.ProducingProvider
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject

fun Application.configureShutdownEvent() {
    val consumingProvider by inject<ConsumingProvider>()
    val producingProvider by inject<ProducingProvider>()
    val cacheService by inject<CacheService>()
    val dispatcher by inject<CoroutineDispatcher>()

    consumingProvider.onShutdown()
    producingProvider.onShutdown()
    CoroutineScope(dispatcher).launch {
        cacheService.onShutdown()
    }
}
