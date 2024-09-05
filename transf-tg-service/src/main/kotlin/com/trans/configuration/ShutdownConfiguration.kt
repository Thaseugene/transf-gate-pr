package com.trans.configuration

import com.trans.service.cache.CacheService
import com.transf.kafka.messaging.service.ConsumingProvider
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject

fun Application.configureShutdownEvent() {
    val consumingProvider by inject<ConsumingProvider>()
    val cacheService by inject<CacheService>()
    val dispatcher by inject<CoroutineDispatcher>()

    consumingProvider.stopConsuming()
    CoroutineScope(dispatcher).launch {
        cacheService.onShutdown()
    }
}
