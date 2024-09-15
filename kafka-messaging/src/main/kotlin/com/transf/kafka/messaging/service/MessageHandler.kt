package com.transf.kafka.messaging.service

import com.transf.kafka.messaging.service.type.HandlerType
import org.apache.kafka.clients.consumer.ConsumerRecord
import kotlin.reflect.KClass

interface MessageHandler<T> {

    fun handleMessage(message: ConsumerRecord<String, T>)

    fun getType(): HandlerType

    fun getGenericType(): Class<T>

}
