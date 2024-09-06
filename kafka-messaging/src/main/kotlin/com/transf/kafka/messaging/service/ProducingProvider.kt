package com.transf.kafka.messaging.service

import com.transf.kafka.messaging.service.type.SenderType
import com.transf.kafka.messaging.configuration.KafkaInnerConfig
import com.transf.kafka.messaging.serder.JsonSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

interface ProducingProvider {

    fun prepareProducerMessaging(kafkaConfig: KafkaInnerConfig)

    fun prepareMessageToSend(requestId: String, message: Any, senderType: SenderType)

    fun onShutdown()

}

class ProducingProviderImpl : ProducingProvider {

    private val logger: Logger = LoggerFactory.getLogger(ProducingProviderImpl::class.java)

    private var senderTypes = mutableMapOf<SenderType, String>()

    private lateinit var producer: KafkaProducer<String, Any>

    override fun prepareProducerMessaging(kafkaConfig: KafkaInnerConfig) {
        kafkaConfig.producerTopics.forEach { entry ->
            senderTypes[enumValueOf(entry.value.senderType)] = entry.key
        }
        producer = createProducer(kafkaConfig)
    }

    override fun prepareMessageToSend(requestId: String, message: Any, senderType: SenderType) {
        senderTypes[senderType]?.let { sendMessage(requestId, message, it) }
    }

    override fun onShutdown() {
        producer.close()
    }

    private fun sendMessage(requestId: String, message: Any, topicName: String) {
        logger.info("Sending message - ${HandlerProvider.objectMapper.writeValueAsString(message)} to topic $topicName")
        producer.send(ProducerRecord(topicName, requestId, message))
    }

    private fun createProducer(kafkaConfig: KafkaInnerConfig): KafkaProducer<String, Any> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaConfig.bootstrapServers
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java.name
        return KafkaProducer(props)
    }

}
