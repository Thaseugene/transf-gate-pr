package storage.trans.com.plugins
//
//import com.fasterxml.jackson.core.type.TypeReference
//import com.trans.domain.EventModel
//import com.trans.domain.EventRecord
//import com.trans.domain.EventRecordExecuteType
//import com.trans.dto.EventResponse
//import com.trans.integration.transacription.TranscriptionService
//import com.trans.serder.JsonDeserializer
//import com.trans.serder.JsonSerializer
//import com.trans.service.MessageService
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import com.trans.service.mapping.toEventModel
//import com.trans.service.mapping.toEventRecord
//import kotlinx.coroutines.*
//import org.apache.kafka.clients.consumer.ConsumerConfig
//import org.apache.kafka.clients.consumer.ConsumerRecord
//import org.apache.kafka.clients.consumer.KafkaConsumer
//import org.apache.kafka.clients.producer.KafkaProducer
//import org.apache.kafka.clients.producer.ProducerConfig
//import org.apache.kafka.clients.producer.ProducerRecord
//import org.apache.kafka.common.serialization.StringDeserializer
//import org.apache.kafka.common.serialization.StringSerializer
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import java.util.*
//
//class KafkaService(
//    private val messageService: MessageService,
//    private val transcriptionService: TranscriptionService,
//    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
//) {
//
//    private val logger: Logger = LoggerFactory.getLogger(KafkaService::class.java)
//
////    val inputConsumer: KafkaConsumer<String, EventRecord> = createConsumer("test-group-id")
//    val transcriptionConsumer: KafkaConsumer<String, EventRecord> = createConsumer("test-transcription-group-id")
////    val inputProducer: KafkaProducer<String, EventRecord> = createProducer()
//    val transcriptionProducer: KafkaProducer<String, EventRecord> = createProducer()
//
//    init {
//        CoroutineScope(dispatcher).launch {
////            launchKafkaConsumerInputTopic(inputConsumer, "test-event-topic")
//            launchKafkaConsumerTranscriptionTopic(transcriptionConsumer, "test-transcription-topic")
//        }
//    }
////
////    fun sendMessage(eventRecord: EventRecord) {
////        logger.info("start sending message ${eventRecord.id}")
////        inputProducer.send(ProducerRecord("test-event-topic", eventRecord))
////    }
//
//    fun sendMessageToTranscription(eventRecord: EventRecord) {
//        transcriptionProducer.send(ProducerRecord("test-transcription-topic", eventRecord))
//    }
//
//    private suspend fun launchKafkaConsumerInputTopic(consumer: KafkaConsumer<String, EventRecord>, topic: String) {
//        coroutineScope {
//            launch(dispatcher) {
//                consumer.subscribe(listOf(topic))
//                try {
//                    while (true) {
//                        val records = consumer.poll(java.time.Duration.ofSeconds(1))
//                        for (record: ConsumerRecord<String, EventRecord> in records) {
//                            logger.info("Consumed message from topic ${record.topic()}: ${record.value().id}")
//                            val executiveMap: Map<EventRecordExecuteType, (EventModel) -> EventResponse> = mapOf(
//                                EventRecordExecuteType.CREATE to { eventModel -> messageService.createEvent(eventModel) },
//                                EventRecordExecuteType.UPDATE to { eventModel -> messageService.updateEvent(eventModel) }
//                            )
//                            record.value()?.toEventModel()?.let {
//                                val eventResp = executiveMap.get(record.value().type)?.invoke(it)
//                                eventResp?.let { resp ->
//                                    sendMessageToTranscription(resp.toEventRecord())
//                                }
//                            }
//                        }
//                    }
//                } catch (e: Exception) {
//                    logger.error("Error occurred while processing message from topic", e)
//                } finally {
//                    consumer.close()
//                }
//            }
//        }
//    }
//
//    private suspend fun launchKafkaConsumerTranscriptionTopic(consumer: KafkaConsumer<String, EventRecord>, topic: String) {
//        coroutineScope {
//            launch(dispatcher) {
//                consumer.subscribe(listOf(topic))
//                try {
//                    while (true) {
//                        val records = consumer.poll(java.time.Duration.ofSeconds(1))
//                        for (record: ConsumerRecord<String, EventRecord> in records) {
//                            logger.info("Consumed message from topic ${record.topic()}: ${record.value().id}")
//                            record.value()?.let {
//                                val message = transcriptionService.tryToMakeTranscript(it.id.toString())
//                                logger.info("$message")
//                            }
//                        }
//                    }
//                } catch (e: Exception) {
//                    logger.error("Error occurred while processing message from topic", e)
//                } finally {
//                    consumer.close()
//                }
//            }
//        }
//    }
//
//    private fun createProducer(): KafkaProducer<String, EventRecord> {
//        val props = Properties()
//        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
//        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
//        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java.name
//        return KafkaProducer(props)
//    }
//
//    private fun createConsumer(groupId: String): KafkaConsumer<String, EventRecord> {
//        val props = Properties()
//        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
//        props[ConsumerConfig.GROUP_ID_CONFIG] = groupId
//        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
//        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java.name
//        props["value.deserializer.type"] = object: TypeReference<EventRecord>() {}
//        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
//        return KafkaConsumer(props)
//    }
//}
//
//
