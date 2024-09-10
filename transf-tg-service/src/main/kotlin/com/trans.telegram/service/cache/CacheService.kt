package com.trans.telegram.service.cache

import com.fasterxml.jackson.core.type.TypeReference
import com.trans.telegram.configuration.RedisConfig
import com.transf.kafka.messaging.service.HandlerProvider
import redis.clients.jedis.JedisPool

class CacheService {

    private var cacheMessageDuration: Long = Long.MAX_VALUE
    var jedisPool: JedisPool? = null

    fun prepareRedisConnection(redisConfig: RedisConfig) {
        jedisPool = JedisPool(redisConfig.address)
        cacheMessageDuration = redisConfig.cacheMessageDuration
    }

    fun insertCacheData(key: String, value: Any) {
        jedisPool?.resource?.use { jedis ->
            jedis.setex(key, cacheMessageDuration, HandlerProvider.objectMapper.writeValueAsString(value))
        }
    }

    inline fun <reified T> retrieveCachedValue(key: String): T? {
        jedisPool?.resource?.use { jedis ->
            val cachedValue = jedis.get(key)
            if (cachedValue != null) {
                return HandlerProvider.objectMapper.readValue(cachedValue, object : TypeReference<T>() {})
            }
        }
        return null
    }

    fun onShutdown() {
        jedisPool?.close()
    }
}
