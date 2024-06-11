package com.trans.service

import com.trans.domain.Event
import io.github.flaxoos.ktor.server.plugins.kafka.components.toRecord
import io.github.flaxoos.ktor.server.plugins.kafka.kafkaProducer
import io.ktor.server.application.*
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class StreamingService {

    private val logger: Logger = LoggerFactory.getLogger(StreamingService::class.java)

    fun handleMessage(event: Event) {
        logger
    }

    fun sendEvent(event: Event) {

    }

    private fun Application.sendEvent(event: Event) {
        log.info("sending event")
        this.kafkaProducer?.send(ProducerRecord(event.topicName, event.requestId, event.toRecord()))
            ?.get(100, TimeUnit.MILLISECONDS)
    }
}
