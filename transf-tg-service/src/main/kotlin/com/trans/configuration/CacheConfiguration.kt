package com.trans.configuration

import com.trans.service.cache.CacheService
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureCache(appConfig: ApplicationConfiguration) {
    val cacheService by inject<CacheService>()

    cacheService.prepareRedisConnection(appConfig.redisConfig)
}
