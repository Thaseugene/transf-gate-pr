package storage.trans.com.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import storage.trans.com.messaging.HandlerType
import storage.trans.com.service.processing.TelegramMessageHandler
import storage.trans.com.service.processing.TranscriptMessageHandler

class HandlerProvider(
    private val messageService: MessageService
) {

    private val handlers = mutableMapOf<HandlerType, Any>()

    companion object {
        val objectMapper: ObjectMapper = with(jacksonObjectMapper()) {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        }
    }

    init {
        val telegramMessageHandler = TelegramMessageHandler(messageService)
        val transcriptMessageHandler = TranscriptMessageHandler(messageService)

        handlers.put(telegramMessageHandler.getType(), telegramMessageHandler)
        handlers.put(transcriptMessageHandler.getType(), transcriptMessageHandler)
    }

    fun retrieveHandler(type: HandlerType): Any? = handlers[type]

}

