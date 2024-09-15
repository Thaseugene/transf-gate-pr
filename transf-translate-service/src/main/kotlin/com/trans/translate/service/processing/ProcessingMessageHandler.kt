package com.trans.translate.service.processing

import com.transf.kafka.messaging.service.MessageHandler
import com.transf.kafka.messaging.service.type.HandlerType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.trans.translate.model.request.TranslateMessageRequest
import com.trans.translate.service.MessageService

class ProcessingMessageHandler (
    private val dispatcher: CoroutineDispatcher,
    private val messageService: MessageService,
): MessageHandler<TranslateMessageRequest> {

    private val logger: Logger = LoggerFactory.getLogger(ProcessingMessageHandler::class.java)

    override fun handleMessage(message: ConsumerRecord<String, TranslateMessageRequest>) {
        CoroutineScope(dispatcher).launch {
            message.value()?.let {
                logger.info("Received Message -> ${message.value()}")
                messageService.processTranslateMessage(message.value())
            }
        }
    }

    override fun getType(): HandlerType = HandlerType.PROCESSING_HANDLER

    override fun getGenericType(): Class<TranslateMessageRequest> = TranslateMessageRequest::class.java

}
