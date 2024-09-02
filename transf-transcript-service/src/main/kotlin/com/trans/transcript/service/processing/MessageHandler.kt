package com.trans.transcript.service.processing

import org.apache.kafka.clients.consumer.ConsumerRecord
import com.trans.transcript.messaging.HandlerType

interface MessageHandler<T> {

    fun handleMessage(message: ConsumerRecord<String, T>)

    fun getType(): HandlerType

}
