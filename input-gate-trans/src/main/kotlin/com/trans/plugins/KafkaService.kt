package com.trans.plugins

import com.trans.domain.MessageModel
import com.trans.domain.EventRecord
import com.trans.domain.EventRecordExecuteType
import com.trans.dto.EventResponse
import com.trans.kafka.JsonDeserializer
import com.trans.kafka.JsonSerializer
import com.trans.service.EventService
import com.trans.service.mapping.toEventModel
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

class KafkaService(
    private val eventService: EventService
) {

    private val logger: Logger = LoggerFactory.getLogger(JsonSerializer::class.java)

    val consumer: KafkaConsumer<String, EventRecord> = createConsumer("test-group-id")
    val producer: KafkaProducer<String, EventRecord> = createProducer()

    init {
        launchKafkaConsumer(consumer, "test-event-topic")
    }

    fun sendMessage(eventRecord: EventRecord) {
        producer.send(ProducerRecord("test-event-topic", eventRecord))
    }

    private fun launchKafkaConsumer(consumer: KafkaConsumer<String, EventRecord>, topic: String) {
        GlobalScope.launch(Dispatchers.IO) {
            consumer.subscribe(listOf(topic))
            try {
                while (true) {
                    val records = consumer.poll(java.time.Duration.ofSeconds(1))
                    for (record: ConsumerRecord<String, EventRecord> in records) {
                        logger.info("Consumed message from topic ${record.topic()}: ${record.value()}")
                        val executiveMap: Map<EventRecordExecuteType, (MessageModel) -> EventResponse> = mapOf(
                            EventRecordExecuteType.CREATE to { eventModel -> eventService.createEvent(eventModel) },
                            EventRecordExecuteType.UPDATE to { eventModel -> eventService.updateEvent(eventModel) }
                        )

                        record.value()?.toEventModel()?.let { executiveMap.get(record.value().type)?.invoke(it) }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                consumer.close()
            }
        }
    }

    private fun createProducer(): KafkaProducer<String, EventRecord> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java.name
        props["value.serializer.class"] = EventRecord::class.java.name // Set the class type here
        return KafkaProducer(props)
    }

    private fun createConsumer(groupId: String): KafkaConsumer<String, EventRecord> {
        val props = Properties()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java.name
        props["value.deserializer.class"] = EventRecord::class.java.name // Set the class type here
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return KafkaConsumer(props)
    }
}


