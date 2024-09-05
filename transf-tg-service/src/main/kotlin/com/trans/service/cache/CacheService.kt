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

    private lateinit var redisClient: RedisClient

    private lateinit var redisConnection: RedisCoroutinesCommands<String, String>

    lateinit var redisCommands: RedisCoroutinesCommands<String, String>

    suspend fun insertCacheData(key: String, value: Any) {
        redisCommands.set(key, HandlerProvider.objectMapper.writeValueAsString(value))
    }

    suspend inline fun <reified T> retrieveCachedValue(key: String): T? {
        val cachedValue = redisCommands.get(key)
        if (cachedValue != null) {
            return HandlerProvider.objectMapper.readValue(cachedValue, object: TypeReference<T>() {})
        }
        return null
    }

    suspend fun onShutdown() {
        redisConnection.shutdown(true)
        redisClient.close()
    }

    fun prepareRedisConnection(redisConfig: RedisConfig) {
        redisClient = RedisClient.create(redisConfig.address)
        redisConnection = redisClient.connect().coroutines().also {
            redisCommands = it
        }
    }

}
