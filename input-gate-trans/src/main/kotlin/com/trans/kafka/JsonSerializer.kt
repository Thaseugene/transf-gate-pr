package com.trans.kafka

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.common.serialization.Serializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class JsonSerializer<T>: Serializer<T> {

    private val logger: Logger = LoggerFactory.getLogger(JsonDeserializer::class.java)
    private lateinit var tClass: Class<T>

    companion object {
        private val mapper: ObjectMapper = jacksonObjectMapper().apply {
            setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        }
    }

    override fun configure(configs: Map<String, *>?, isKey: Boolean) {
        val className = configs?.get("value.serializer.class") as? String
        if (className != null) {
            tClass = Class.forName(className) as Class<T>
        }
    }

    override fun serialize(topic: String?, data: T?): ByteArray? {
        if (data == null) return null
        return try {
             mapper.writeValueAsBytes(data)
        } catch (e: Exception) {
            logger.error(e.message, e)
            null
        }
    }
}
