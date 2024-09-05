package com.trans.transcript.service

import com.trans.transcript.integration.client.HttpClientService
import com.trans.transcript.service.processing.ProcessingMessageHandler
import com.transf.kafka.messaging.service.HandlerProvider
import com.transf.kafka.messaging.service.type.HandlerType
import kotlinx.coroutines.CoroutineDispatcher

class HandlerProviderImpl(
    private val dispatcher: CoroutineDispatcher,
    private val messageService: MessageService,
    private val clientService: HttpClientService
): HandlerProvider {

    private val handlers = mutableMapOf<HandlerType, Any>()

    init {
        ProcessingMessageHandler(dispatcher, messageService, clientService).also {
            handlers[it.getType()] = it
        }
    }

    override fun retrieveHandler(type: HandlerType): Any? = handlers[type]

}
