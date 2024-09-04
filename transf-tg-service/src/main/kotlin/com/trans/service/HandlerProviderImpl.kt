package com.trans.service

import com.trans.integration.tg.BotService
import com.trans.service.processing.ProcessingMessageHandler
import com.transf.kafka.messaging.HandlerType
import com.transf.kafka.messaging.service.HandlerProvider
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.java.KoinJavaComponent.inject

class HandlerProviderImpl: HandlerProvider {

    private val dispatcher: CoroutineDispatcher by inject(CoroutineDispatcher::class.java)

    private val botService: BotService by inject(BotService::class.java)

    private val handlers = mutableMapOf<HandlerType, Any>()

    init {
        ProcessingMessageHandler(botService, dispatcher).also {
            handlers[it.getType()] = it
        }
    }

    override fun retrieveHandler(type: HandlerType): Any? = handlers[type]

}

