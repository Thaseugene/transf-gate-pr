package com.trans.telegram.service.processing

import com.trans.telegram.model.MessageStatus
import com.trans.telegram.model.response.ProcessingMessageResponse
import com.trans.telegram.integration.tg.BotService
import com.transf.kafka.messaging.service.MessageHandler
import com.transf.kafka.messaging.service.type.HandlerType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProcessingMessageHandler(
    private val botService: BotService,
    private val dispatcher: CoroutineDispatcher
) : MessageHandler<ProcessingMessageResponse> {

    private val logger: Logger = LoggerFactory.getLogger(ProcessingMessageHandler::class.java)

    override fun handleMessage(message: ConsumerRecord<String, ProcessingMessageResponse>) {
        CoroutineScope(dispatcher).launch {
            message.value()?.let {
                try {
                    logger.info("Handled message -> ${message.value()}")
                    if (it.translatedResult != null && it.status != null && it.status.equals(MessageStatus.OK) && it.chatId != null && it.messageId != null) {
                        botService.sendSuccessTranslateMessage(it.translatedResult, it.chatId, it.messageId)
                        return@launch;
                    }
                    if (it.result != null && it.status != null && it.status.equals(MessageStatus.OK) && it.chatId != null && it.messageId != null) {
                        botService.sendSuccessTranscriptMessage(it.result, it.chatId, it.messageId, message.key())
                        return@launch;
                    }
                    botService.sendErrorMessage(message.key())
                } catch (ex: Exception) {
                    logger.error("Error handled while sending answer to tg service", ex)
                }

            }
        }
    }

    override fun getType(): HandlerType {
        return HandlerType.PROCESSING_HANDLER
    }

}
