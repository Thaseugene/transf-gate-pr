package com.trans.configuration

import io.ktor.server.application.*

class ApplicationConfiguration {
    lateinit var kafkaConfig: KafkaInnerConfig
}

fun Application.configureApplication(): ApplicationConfiguration {
    val appConfig = ApplicationConfiguration()

    //kafka config import
    val kafkaConfigObject = environment.config.config("kafka")
    val bootstrapServers = kafkaConfigObject.property("bootstrapServers").getList()
    val groupId = kafkaConfigObject.property("groupId").getString()
    val producerTopics = kafkaConfigObject.configList("producerTopics").associate {
        val producerInnerConfig = ProducerInnerConfig(
            it.property("name").getString(),
            it.property("senderType").getString()
        )
        producerInnerConfig.topicName to producerInnerConfig
    }
    val consumerTopics = kafkaConfigObject.configList("consumerTopics").associate {
        val consumerInnerConfig = ConsumerInnerConfig(
            it.property("name").getString(),
            it.property("handler").getString(),
            it.property("deserializerType").getString(),
        )
        consumerInnerConfig.topicName to consumerInnerConfig
    }

    appConfig.kafkaConfig = KafkaInnerConfig(groupId, bootstrapServers, consumerTopics, producerTopics)

    return appConfig
}

data class KafkaInnerConfig(
    val groupId: String,
    val bootstrapServers: List<String>,
    val consumerConfig: Map<String, ConsumerInnerConfig>,
    val producerTopics: Map<String, ProducerInnerConfig>
)

data class ConsumerInnerConfig(
    val topicName: String,
    val handlerName: String,
    val deserializerType: String
)

data class ProducerInnerConfig(
    val topicName: String,
    val senderType: String
)
