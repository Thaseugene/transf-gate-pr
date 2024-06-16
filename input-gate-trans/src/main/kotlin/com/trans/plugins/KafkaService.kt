package com.trans.plugins

import com.trans.domain.Event
import com.trans.kafka.JsonDeserializer
import com.trans.kafka.JsonSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class KafkaService() {

    private val logger: Logger = LoggerFactory.getLogger(JsonSerializer::class.java)

    val consumer: KafkaConsumer<String, Event> = createConsumer("test-group-id")
    val producer: KafkaProducer<String, Event> = createProducer()

    init {
        launchKafkaConsumer(consumer, "test-event-topic")
    }

    fun sendMessage(event: Event) {
        producer.send(ProducerRecord("test-event-topic", event))
    }

    private fun launchKafkaConsumer(consumer: KafkaConsumer<String, Event>, topic: String) {
        GlobalScope.launch(Dispatchers.IO) {
            consumer.subscribe(listOf(topic))
            try {
                while (true) {
                    val records = consumer.poll(java.time.Duration.ofSeconds(1))
                    for (record: ConsumerRecord<String, Event> in records) {
                        // Handle each record
                        logger.info("Consumed message from topic ${record.topic()}: ${record.value()}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                consumer.close()
            }
        }
    }

    private fun createProducer(): KafkaProducer<String, Event> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java.name
        props["value.serializer.class"] = Event::class.java.name // Set the class type here
        return KafkaProducer(props)
    }

    private fun createConsumer(groupId: String): KafkaConsumer<String, Event> {
        val props = Properties()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java.name
        props["value.deserializer.class"] = Event::class.java.name // Set the class type here
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return KafkaConsumer(props)
    }
}


