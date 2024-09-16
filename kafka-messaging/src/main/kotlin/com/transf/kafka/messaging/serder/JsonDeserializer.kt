package com.transf.kafka.messaging.serder

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class JsonDeserializer<T> : Deserializer<T> {

    private val logger: Logger = LoggerFactory.getLogger(JsonDeserializer::class.java)
    private lateinit var clazz: Class<T>

    companion object {
        private val mapper: ObjectMapper = jacksonObjectMapper().apply {
            setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun configure(configs: Map<String, *>?, isKey: Boolean) {
        val typeValue = configs?.get("value.deserializer.type") as? Class<*>
        if (typeValue != null) {
            clazz = typeValue as Class<T>
        }
    }

    override fun deserialize(topic: String?, data: ByteArray?): T? {
        if (data == null) return null
        return try {
            mapper.readValue(data, clazz)
        } catch (e: Exception) {
            logger.error(e.message, e)
            null
        }
    }
}
