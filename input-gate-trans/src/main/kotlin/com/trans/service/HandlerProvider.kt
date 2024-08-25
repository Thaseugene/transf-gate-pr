package com.trans.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.trans.integration.tg.BotService
import com.trans.service.processing.ProcessingMessageHandler
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.java.KoinJavaComponent.inject
import storage.trans.com.messaging.HandlerType


class HandlerProvider(
    private val dispatcher: CoroutineDispatcher,
    private val botService: Lazy<BotService>
) {
    private val handlers = mutableMapOf<HandlerType, Any>()

    companion object {
        val objectMapper: ObjectMapper = with(jacksonObjectMapper()) {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        }
    }

    init {
        val telegramMessageHandler = ProcessingMessageHandler(botService.value, dispatcher)

        handlers[telegramMessageHandler.getType()] = telegramMessageHandler
    }

    fun retrieveHandler(type: HandlerType): Any? = handlers[type]

}

