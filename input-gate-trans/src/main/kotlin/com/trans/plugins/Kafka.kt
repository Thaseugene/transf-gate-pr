package com.trans.plugins

import com.trans.configuration.KafkaInnerConfig
import io.github.flaxoos.ktor.server.plugins.kafka.*
import io.ktor.client.*
import io.ktor.server.application.*

fun Application.configureKafka(kafkaConfig: KafkaInnerConfig) {
    install(Kafka) {
        schemaRegistryUrl = kafkaConfig.registryUrl
        val topicName = TopicName.named(kafkaConfig.topicName)
        topic(topicName) {
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
        configureConsumers(topicName)
        registerSchemas {
            using { // <-- optionally provide a client, by default CIO is used
                HttpClient()
            }
            // MyRecord::class at myTopic // <-- Will register schema upon startup
        }
    }
}

fun Application.sendUser(str: String) {
    this.kafkaProducer
}

fun Application.handleMessage() {
//    this.kafkaConsumer.
}

private fun KafkaConfig.configureConsumers(topicName: TopicName) {
    consumerConfig {
        consumerRecordHandler(topicName) { record ->
            // Do something with record
        }

    }
}
