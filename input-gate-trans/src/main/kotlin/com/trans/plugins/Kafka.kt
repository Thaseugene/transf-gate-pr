//package com.trans.plugins
//
//import com.trans.configuration.KafkaInnerConfig
//import com.trans.domain.Event
//import io.github.flaxoos.ktor.server.plugins.kafka.*
//import io.github.flaxoos.ktor.server.plugins.kafka.components.fromRecord
//import io.github.flaxoos.ktor.server.plugins.kafka.components.toRecord
//import io.ktor.server.application.*
//import org.apache.kafka.clients.producer.ProducerRecord
//import java.util.concurrent.TimeUnit
//
//fun Application.configureKafka(kafkaConfig: KafkaInnerConfig) {
//    install(Kafka) {
//        schemaRegistryUrl = kafkaConfig.registryUrl
//        val eventTopic = TopicName.named(kafkaConfig.topicName)
//        configureTopic(eventTopic)
//        configureCommonSettings(kafkaConfig)
//        configureProducer(kafkaConfig)
//        configureConsumer(kafkaConfig)
//        configureConsumerHandling(eventTopic)
//        configureRegisterSchemas(eventTopic)
//    }
//}
//
//private fun KafkaConfig.configureRegisterSchemas(eventTopic: TopicName) {
//    registerSchemas {
//        Event::class at eventTopic
//    }
//}
//
//private fun KafkaConfig.configureConsumer(kafkaConfig: KafkaInnerConfig) {
//    consumer { // <-- Creates a consumer
//        groupId = kafkaConfig.groupId
//        clientId = kafkaConfig.consumerClientId //<-- Override common properties
//    }
//}
//
//private fun KafkaConfig.configureProducer(kafkaConfig: KafkaInnerConfig) {
//    producer {
//        clientId = kafkaConfig.producerClientId
//    }
//}
//
//private fun KafkaConfig.configureCommonSettings(kafkaConfig: KafkaInnerConfig) {
//    common { // <-- Define common properties
//        bootstrapServers = kafkaConfig.bootstrapServers
//        retries = 1
//        clientId = kafkaConfig.producerClientId
//    }
//}
//
//private fun KafkaConfig.configureTopic(eventTopic: TopicName) {
//    topic(eventTopic) {
//        partitions = 1
//        replicas = 1
//        configs {
//            messageTimestampType = MessageTimestampType.CreateTime
//        }
//    }
//}
//
//private fun KafkaConfig.configureConsumerHandling(topicName: TopicName) {
//    consumerConfig {
//        consumerRecordHandler(topicName) { record ->
//            val event = fromRecord<Event>(record.value())
//            log.info("Event is recorded - $event")
//        }
//    }
//
//}
