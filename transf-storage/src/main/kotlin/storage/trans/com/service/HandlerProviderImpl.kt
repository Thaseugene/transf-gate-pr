package storage.trans.com.service

import com.transf.kafka.messaging.service.HandlerProvider
import com.transf.kafka.messaging.service.type.HandlerType
import storage.trans.com.service.processing.TelegramMessageHandler
import storage.trans.com.service.processing.TranscriptMessageHandler

class HandlerProviderImpl(
    private val messageService: MessageService
): HandlerProvider {

    private val handlers = mutableMapOf<HandlerType, Any>()

    init {
        TelegramMessageHandler(messageService).also {
            handlers[it.getType()] = it
        }
        TranscriptMessageHandler(messageService).also {
            handlers[it.getType()] = it
        }
    }

    override fun retrieveHandler(type: HandlerType): Any? = handlers[type]

}

