package com.trans.configuration

import com.transf.kafka.messaging.configuration.ConsumerInnerConfig
import com.transf.kafka.messaging.configuration.KafkaInnerConfig
import com.transf.kafka.messaging.configuration.ProducerInnerConfig
import io.ktor.server.application.*

class ApplicationConfiguration {
    lateinit var kafkaConfig: KafkaInnerConfig
    lateinit var redisConfig: RedisConfig
}

fun Application.configureApplication(): ApplicationConfiguration {
    val appConfig = ApplicationConfiguration()

    //kafka config import
    val kafkaConfigObject = environment.config.config("kafka")
    val bootstrapServers = kafkaConfigObject.property("bootstrapServers").getList()
    val groupId = kafkaConfigObject.property("groupId").getString()
    val producerTopics =  kafkaConfigObject.configList("producerTopics").associate {
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

    val redisConfigObject = environment.config.config("redis")
    val address = redisConfigObject.property("address").getString()

    appConfig.kafkaConfig = KafkaInnerConfig(groupId, bootstrapServers, consumerTopics, producerTopics)
    appConfig.redisConfig = RedisConfig(address)

    return appConfig
}

data class RedisConfig(
    val address: String
)
