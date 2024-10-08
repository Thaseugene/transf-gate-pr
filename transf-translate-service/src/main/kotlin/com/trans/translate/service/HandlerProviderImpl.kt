package com.trans.translate.service

import com.trans.translate.service.processing.ProcessingMessageHandler
import com.transf.kafka.messaging.service.HandlerProvider
import com.transf.kafka.messaging.service.type.HandlerType
import kotlinx.coroutines.CoroutineDispatcher

class HandlerProviderImpl(
    private val dispatcher: CoroutineDispatcher,
    private val messageService: MessageService,
) : HandlerProvider {

    private val handlers = mutableMapOf<HandlerType, Any>()

    init {
        ProcessingMessageHandler(dispatcher, messageService).also {
            handlers[it.getType()] = it
        }
    }

    override fun retrieveHandler(type: HandlerType): Any? = handlers[type]

}
