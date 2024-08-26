package storage.trans.com.messaging

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
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import storage.trans.com.configuration.KafkaInnerConfig
import storage.trans.com.service.MessageProcessor
import java.util.*

fun Application.configureMessaging(kafkaConfig: KafkaInnerConfig) {
    val messagingProvider by inject<MessagingProvider>()
    val dispatcher by inject<CoroutineDispatcher>()

    CoroutineScope(dispatcher).launch {
        messagingProvider.prepareConsumerMessaging(kafkaConfig)
        messagingProvider.prepareProducerMessaging(kafkaConfig)
    }
}

class MessagingProvider(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val messageProcessor: MessageProcessor
) {

    private val logger: Logger = LoggerFactory.getLogger(MessagingProvider::class.java)

    private lateinit var producer: KafkaProducer<String, String>

    fun prepareProducerMessaging(kafkaConfig: KafkaInnerConfig) {
        producer = createProducer(kafkaConfig)
    }

    suspend fun prepareConsumerMessaging(kafkaConfig: KafkaInnerConfig) {
        launchKafkaConsumerTranscriptionTopic(createConsumer(kafkaConfig), kafkaConfig.topicNames)
    }

    fun sendTranscriptMessage(message: String, topicName: String) {
        producer.send(ProducerRecord(topicName, message))
    }

    private suspend fun launchKafkaConsumerTranscriptionTopic(
        consumer: KafkaConsumer<String, String>,
        topics: List<String>
    ) {
        coroutineScope {
            launch(dispatcher) {
                consumer.subscribe(topics)
                try {
                    while (true) {
                        val records = consumer.poll(java.time.Duration.ofSeconds(1))
                        for (record: ConsumerRecord<String, String> in records) {
                            logger.info("Consumed message from topic ${record.topic()}")
                            record.value()?.let {
                                messageProcessor.processIncomingMessage(record)
                            }
                        }
                    }
                } catch (e: Exception) {
                    logger.error("Error occurred while processing message from topic", e)
                } finally {
                    consumer.close()
                }
            }
        }
    }

    private fun createConsumer(kafkaConfig: KafkaInnerConfig): KafkaConsumer<String, String> {
        val props = Properties()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaConfig.bootstrapServers
        props[ConsumerConfig.GROUP_ID_CONFIG] = "storage-group"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return KafkaConsumer(props)
    }

    private fun createProducer(kafkaConfig: KafkaInnerConfig): KafkaProducer<String, String> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaConfig.bootstrapServers
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        return KafkaProducer(props)
    }

}
