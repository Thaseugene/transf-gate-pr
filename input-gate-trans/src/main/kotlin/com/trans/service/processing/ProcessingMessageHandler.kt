package com.trans.service.processing

import com.trans.domain.ProcessingMessageResponse
import com.trans.integration.tg.BotService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import storage.trans.com.messaging.HandlerType

class ProcessingMessageHandler(
    private val botService: BotService,
    private val dispatcher: CoroutineDispatcher
) : MessageHandler<ProcessingMessageResponse> {

    private val logger: Logger = LoggerFactory.getLogger(ProcessingMessageHandler::class.java)

    override fun handleMessage(message: ConsumerRecord<String, ProcessingMessageResponse>) {
        CoroutineScope(dispatcher).launch {
            message.value()?.let {
                logger.info("Message -> ${message.value()}")
                botService.sendAnswer(it.result, it.chatId, it.messageId)
            }
        }
    }

    override fun getType(): HandlerType {
        return HandlerType.PROCESSING_HANDLER
    }

}
