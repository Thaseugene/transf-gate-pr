package com.transf.kafka.messaging.configuration

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
