package com.trans.transcript.service

import com.trans.transcript.integration.transacription.TranscriptionService
import com.trans.transcript.service.processing.ProcessingMessageHandler
import com.transf.kafka.messaging.HandlerType
import com.transf.kafka.messaging.MessagingProvider
import com.transf.kafka.messaging.service.HandlerProvider
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.java.KoinJavaComponent.inject

class HandlerProviderImpl: HandlerProvider {

    private val dispatcher: CoroutineDispatcher by inject(CoroutineDispatcher::class.java)

    private val transcriptionService : TranscriptionService by inject(TranscriptionService::class.java)

    private val provider: MessagingProvider by inject(MessagingProvider::class.java)

    private val handlers = mutableMapOf<HandlerType, Any>()

    init {
        ProcessingMessageHandler(dispatcher, transcriptionService, provider).also {
            handlers[it.getType()] = it
        }
    }

    override fun retrieveHandler(type: HandlerType): Any? = handlers[type]

}
