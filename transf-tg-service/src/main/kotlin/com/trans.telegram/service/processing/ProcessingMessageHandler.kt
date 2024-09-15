package com.trans.telegram.service.processing

import com.trans.telegram.service.tg.BotService
import com.trans.telegram.model.MessageStatus
import com.trans.telegram.model.response.TelegramMessageResponse
import com.trans.telegram.service.tg.BotMessageProcessor
import com.transf.kafka.messaging.service.MessageHandler
import com.transf.kafka.messaging.service.type.HandlerType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProcessingMessageHandler(
    private val processor: BotMessageProcessor,
    private val dispatcher: CoroutineDispatcher
) : MessageHandler<TelegramMessageResponse> {

    private val logger: Logger = LoggerFactory.getLogger(ProcessingMessageHandler::class.java)

    override fun handleMessage(message: ConsumerRecord<String, TelegramMessageResponse>) {
        CoroutineScope(dispatcher).launch {
            message.value()?.let {
                runCatching {
                    logger.info("Handled message -> ${message.value()}")
                    if (it.translatedResult != null && it.status == MessageStatus.OK && it.chatId != null && it.messageId != null) {
                        processor.sendSuccessTranslateMessage(it)
                        return@launch;
                    }
                    if (it.result != null && it.status == MessageStatus.OK && it.chatId != null && it.messageId != null) {
                        processor.sendSuccessTranscriptMessage(it.result, it.chatId, it.messageId, message.key())
                        return@launch;
                    }
                    processor.sendErrorMessage(message.key())
                }.onFailure {
                    logger.error("Error handled while sending answer to tg service", it)
                }
            }
        }
    }

    override fun getType(): HandlerType = HandlerType.PROCESSING_HANDLER

    override fun getGenericType(): Class<TelegramMessageResponse> = TelegramMessageResponse::class.java

}
