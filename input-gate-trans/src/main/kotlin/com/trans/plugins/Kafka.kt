package com.trans.plugins

import com.trans.configuration.KafkaInnerConfig
import com.trans.domain.Event
import com.trans.dto.EventRecord
import io.github.flaxoos.ktor.server.plugins.kafka.*
import io.github.flaxoos.ktor.server.plugins.kafka.components.fromRecord
import io.github.flaxoos.ktor.server.plugins.kafka.components.toRecord
import io.ktor.client.*
import io.ktor.server.application.*
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.concurrent.TimeUnit

fun Application.configureKafka(kafkaConfig: KafkaInnerConfig) {
    install(Kafka) {
        schemaRegistryUrl = kafkaConfig.registryUrl
        val eventTopic = TopicName.named(kafkaConfig.topicName)
        topic(eventTopic) {
            partitions = 1
            replicas = 1
            configs {
                messageTimestampType = MessageTimestampType.CreateTime
            }
        }
        common { // <-- Define common properties
            bootstrapServers = listOf(kafkaConfig.bootstrapServers)
            retries = 1
            clientId = kafkaConfig.producerClientId
        }
        producer { // <-- Creates a producer
            clientId = kafkaConfig.producerClientId
        }
        consumer { // <-- Creates a consumer
            groupId = kafkaConfig.groupId
            clientId = kafkaConfig.consumerClientId //<-- Override common properties
        }
        configureConsumers(eventTopic)
        registerSchemas {
            Event::class at eventTopic
        }
    }
}

fun Application.sendEvent(event: Event) {
    this.kafkaProducer?.send(ProducerRecord(event.topicName, event.requestId, event.toRecord()))?.get(100, TimeUnit.MILLISECONDS)
}

fun Application.handleMessage() {
//    this.kafkaConsumer.
}

private fun KafkaConfig.configureConsumers(topicName: TopicName) {
    consumerConfig {
        consumerRecordHandler(topicName) { record ->
            val event = fromRecord<Event>(record.value())
            log.info("Event is recorded - $event")
        }

    }
}
