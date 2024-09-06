package com.trans.service.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.trans.configuration.RedisConfig
import com.transf.kafka.messaging.service.HandlerProvider
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands

@OptIn(ExperimentalLettuceCoroutinesApi::class)
class CacheService {

    private var cacheMessageDuration: Long = Long.MAX_VALUE

    private var redisClient: RedisClient? = null

    private var redisConnection: RedisCoroutinesCommands<String, String>? = null

    var redisCommands: RedisCoroutinesCommands<String, String>? = null

    suspend fun insertCacheData(key: String, value: Any) {
        redisCommands?.setex(key, cacheMessageDuration, HandlerProvider.objectMapper.writeValueAsString(value))
    }

    suspend inline fun <reified T> retrieveCachedValue(key: String): T? {
        val cachedValue = redisCommands?.get(key)
        if (cachedValue != null) {
            return HandlerProvider.objectMapper.readValue(cachedValue, object: TypeReference<T>() {})
        }
        return null
    }

    suspend fun onShutdown() {
        redisConnection?.shutdown(true)
        redisClient?.close()
    }

    fun prepareRedisConnection(redisConfig: RedisConfig) {
        redisClient = RedisClient.create(redisConfig.address)
        redisClient?.let {
            redisConnection = it.connect().coroutines().also { connection ->
                redisCommands = connection
            }
        }
        cacheMessageDuration = redisConfig.cacheMessageDuration
    }

}
