package com.trans.transcript.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.trans.transcript.integration.transacription.TranscriptionService
import com.trans.transcript.messaging.HandlerType
import com.trans.transcript.messaging.MessagingProvider
import com.trans.transcript.service.processing.ProcessingMessageHandler
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.java.KoinJavaComponent.inject

class HandlerProvider(
    private val dispatcher: CoroutineDispatcher
) {
    private val transcriptionService : TranscriptionService by inject(TranscriptionService::class.java)

    private val provider: MessagingProvider  by inject(MessagingProvider::class.java)

    private val handlers = mutableMapOf<HandlerType, Any>()

    companion object {
        val objectMapper: ObjectMapper = with(jacksonObjectMapper()) {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        }
    }

    init {
        val telegramMessageHandler = ProcessingMessageHandler(dispatcher, transcriptionService, provider)
        handlers[telegramMessageHandler.getType()] = telegramMessageHandler
    }

    fun retrieveHandler(type: HandlerType): Any? = handlers[type]

}
