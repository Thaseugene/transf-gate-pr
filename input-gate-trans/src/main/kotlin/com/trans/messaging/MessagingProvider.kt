package com.trans.messaging

import com.trans.configuration.ConsumerInnerConfig
import com.trans.configuration.KafkaInnerConfig
import com.trans.serder.JsonDeserializer
import com.trans.serder.JsonSerializer
import com.trans.service.HandlerProvider
import com.trans.service.processing.MessageHandler
import io.ktor.server.application.*
import kotlinx.coroutines.*
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.koin.java.KoinJavaComponent.inject
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import storage.trans.com.messaging.SenderType

import java.util.*

fun Application.configureMessaging(kafkaConfig: KafkaInnerConfig) {
    val messagingProvider by inject<MessagingProvider>()
    val dispatcher by inject<CoroutineDispatcher>()

    CoroutineScope(dispatcher).launch {
        messagingProvider.prepareConsumerMessaging(kafkaConfig)
        messagingProvider.prepareProducerMessaging(kafkaConfig)
    }
}

@Suppress("UNCHECKED_CAST")
class MessagingProvider(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val handlerProvider by inject<HandlerProvider>(HandlerProvider::class.java)
    private val logger: Logger = LoggerFactory.getLogger(MessagingProvider::class.java)
    private lateinit var producer: KafkaProducer<String, Any>
    private var senderTypes = mutableMapOf<SenderType, String>()

    fun prepareProducerMessaging(kafkaConfig: KafkaInnerConfig) {
        kafkaConfig.producerTopics.forEach { entry ->
            senderTypes[enumValueOf(entry.value.senderType)] = entry.key
        }
        producer = createProducer(kafkaConfig)
    }

    fun prepareConsumerMessaging(kafkaConfig: KafkaInnerConfig) {
        kafkaConfig.consumerConfig.forEach { entry ->
            CoroutineScope(dispatcher).launch {
                val consumer = createConsumer<Any>(entry.value, kafkaConfig.bootstrapServers, kafkaConfig.groupId)
                launchMessagesConsuming(consumer, entry.value.handlerName, entry.key)
            }
        }
    }

    fun prepareMessageToSend(requestId: String, message: Any, senderType: SenderType) {
        senderTypes[senderType]?.let { sendMessage(requestId, message, it) }
    }


    private fun sendMessage(requestId: String, message: Any, topicName: String) {
        logger.info("Sending message - ${HandlerProvider.objectMapper.writeValueAsString(message)} to topic $topicName")
        producer.send(ProducerRecord(topicName, requestId, message))
    }

    private fun <T> launchMessagesConsuming(
        consumer: KafkaConsumer<String, T>,
        handlerName: String,
        topicName: String
    ) {
        consumer.subscribe(listOf(topicName))
        while (true) {
            try {
                val records = consumer.poll(java.time.Duration.ofSeconds(1))
                for (record: ConsumerRecord<String, T> in records) {
                    logger.info("Consumed message from topic ${record.topic()}")
                    handlerProvider.retrieveHandler(enumValueOf(handlerName))?.let {
                        (it as MessageHandler<T>).handleMessage(record)
                    }
                }
            } catch (e: Exception) {
                logger.error("Error occurred while processing message from topic", e)
            }
        }
    }

    private fun <T> createConsumer(
        consumerConfig: ConsumerInnerConfig,
        bootstrapServers: List<String>,
        groupId: String
    ): KafkaConsumer<String, T> {
        val props = Properties()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java.name
        props["value.deserializer.type"] = Class.forName(consumerConfig.deserializerType) as Class<T>
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"
        return KafkaConsumer(props)
    }

    private fun createProducer(kafkaConfig: KafkaInnerConfig): KafkaProducer<String, Any> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaConfig.bootstrapServers
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java.name
        return KafkaProducer(props)
    }

}
