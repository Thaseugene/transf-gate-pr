package com.trans.service.processing

import org.apache.kafka.clients.consumer.ConsumerRecord
import storage.trans.com.messaging.HandlerType

interface MessageHandler<T> {

    fun handleMessage(message: ConsumerRecord<String, T>)

    fun getType(): HandlerType

}
