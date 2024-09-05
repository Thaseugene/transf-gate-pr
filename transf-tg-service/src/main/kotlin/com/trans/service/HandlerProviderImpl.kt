package com.trans.service

import com.trans.integration.tg.BotService
import com.trans.service.processing.ProcessingMessageHandler
import com.transf.kafka.messaging.service.HandlerProvider
import com.transf.kafka.messaging.service.type.HandlerType
import kotlinx.coroutines.CoroutineDispatcher

class HandlerProviderImpl(
    private val dispatcher: CoroutineDispatcher,
    private val botService: BotService
): HandlerProvider {

    private val handlers = mutableMapOf<HandlerType, Any>()

    init {
        ProcessingMessageHandler(botService, dispatcher).also {
            handlers[it.getType()] = it
        }
    }

    override fun retrieveHandler(type: HandlerType): Any? = handlers[type]

}

