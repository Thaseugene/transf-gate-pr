package storage.trans.com.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import storage.trans.com.domain.TelegramMessageRequest
import storage.trans.com.domain.TranscriptMessageRequest

class MessageProcessor(
    private val messageService: MessageService
) {

    companion object {
        val objectMapper: ObjectMapper = with(jacksonObjectMapper()) {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        }
    }

    fun processIncomingMessage(record: ConsumerRecord<String, String>) {
        when (record.topic()) {
            "transcript-incoming-storage" -> {
                messageService.processIncomingMessage(
                    objectMapper.readValue(
                        record.value(),
                        object : TypeReference<TelegramMessageRequest>() {})
                )
            }

            "tg-incoming-storage" -> {
                messageService.processIncomingTranscriptMessage(
                    objectMapper.readValue(
                        record.value(),
                        object : TypeReference<TranscriptMessageRequest>() {})
                )
            }
        }
    }

}

