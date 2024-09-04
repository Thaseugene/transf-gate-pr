package com.trans.transcript.service

import com.trans.transcript.integration.client.HttpClientService
import com.trans.transcript.integration.transacription.TranscriptionService
import com.trans.transcript.service.processing.ProcessingMessageHandler
import com.transf.kafka.messaging.HandlerType
import com.transf.kafka.messaging.MessagingProvider
import com.transf.kafka.messaging.service.HandlerProvider
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.java.KoinJavaComponent.inject

class HandlerProviderImpl(
    private val dispatcher: CoroutineDispatcher
): HandlerProvider {

    private val messageService: MessageService by inject(MessageService::class.java)

    private val clientService: HttpClientService by inject(HttpClientService::class.java)

    private val handlers = mutableMapOf<HandlerType, Any>()

    init {
        ProcessingMessageHandler(dispatcher, messageService, clientService).also {
            handlers[it.getType()] = it
        }
    }

    override fun retrieveHandler(type: HandlerType): Any? = handlers[type]

}
