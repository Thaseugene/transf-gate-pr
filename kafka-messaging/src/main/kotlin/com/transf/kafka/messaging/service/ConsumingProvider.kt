package com.transf.kafka.messaging.service

import com.transf.kafka.messaging.configuration.ConsumerInnerConfig
import com.transf.kafka.messaging.configuration.KafkaInnerConfig
import com.transf.kafka.messaging.serder.JsonDeserializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

interface ConsumingProvider {

    fun prepareConsumerMessaging(kafkaConfig: KafkaInnerConfig)

    fun onShutdown()

}

@Suppress("UNCHECKED_CAST")
class ConsumingProviderImpl(
    private val dispatcher: CoroutineDispatcher,
    private val handlerProvider: HandlerProvider
) : ConsumingProvider {

    private val logger: Logger = LoggerFactory.getLogger(ConsumingProviderImpl::class.java)

    private val VALUE_DESERIALIZER_TYPE: String = "value.deserializer.type"

    private var isShutDown: Boolean = false;

    override fun prepareConsumerMessaging(kafkaConfig: KafkaInnerConfig) {
        kafkaConfig.consumerConfig.forEach { entry ->
            CoroutineScope(dispatcher).launch {
                val consumer = createConsumer<Any>(entry.value, kafkaConfig.bootstrapServers, kafkaConfig.groupId)
                launchMessagesConsuming(consumer, entry.value.handlerName, entry.key)
            }
        }
    }

    override fun onShutdown() {
        isShutDown = true
    }

    private fun <T> launchMessagesConsuming(
        consumer: KafkaConsumer<String, T>,
        handlerName: String,
        topicName: String
    ) {
        consumer.use { kafkaConsumer ->
            kafkaConsumer.subscribe(listOf(topicName))
            while (!isShutDown) {
                runCatching {
                    val records = kafkaConsumer.poll(java.time.Duration.ofSeconds(1))
                    for (record: ConsumerRecord<String, T> in records) {
                        logger.info("Consumed message from topic ${record.topic()}")
                        handlerProvider.retrieveHandler(enumValueOf(handlerName))?.let {
                            (it as MessageHandler<T>).handleMessage(record)
                        }
                    }
                }.onFailure {
                    logger.error("Error occurred while processing message from topic", it)
                }
            }
            logger.info("Message consuming stopped")
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
        handlerProvider.retrieveHandler(enumValueOf(consumerConfig.handlerName)).let {
            props[VALUE_DESERIALIZER_TYPE] = (it as MessageHandler<T>).getGenericType()
        }
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"

        return KafkaConsumer(props)
    }

}
