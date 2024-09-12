package com.trans.telegram.service

import com.trans.telegram.service.cache.CacheService
import com.trans.telegram.service.mapping.toProcessingMessage
import com.transf.kafka.messaging.common.model.request.CommandStrategy
import com.transf.kafka.messaging.service.ProducingProvider
import com.transf.kafka.messaging.service.type.SenderType
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.MediaContent
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface MessageService {

    fun processTelegramMessage(incomingMessage: ContentMessage<MediaContent>, downloadFilePath: String)

    fun processTranslateMessage(incomingMessage: MessageDataCallbackQuery, lang: String, requestId: String)

}

class MessageServiceImpl(
    private val dispatcher: CoroutineDispatcher,
    private val producingProvider: ProducingProvider,
    private val cacheService: CacheService
) : MessageService {

    private val logger: Logger = LoggerFactory.getLogger(MessageService::class.java)

    override fun processTelegramMessage(
        incomingMessage: ContentMessage<MediaContent>,
        downloadFilePath: String
    ) {
        logger.info("Preparing incoming message from user -> ${incomingMessage.chat.id.chatId.long}")
        incomingMessage.toProcessingMessage(downloadFilePath, CommandStrategy.TRANSCRIPTION).also {
            producingProvider.prepareMessageToSend(
                it.requestId,
                it,
                SenderType.PROCESSING_SENDER
            )
            CoroutineScope(dispatcher).launch {
                cacheService.insertCacheData(it.requestId, it)
            }
        }
    }

    override fun processTranslateMessage(
        incomingMessage: MessageDataCallbackQuery,
        lang: String,
        requestId: String
    ) {
        logger.info("Preparing incoming message from user -> ${incomingMessage.from.id.chatId.long}")
        incomingMessage.toProcessingMessage(requestId, lang, CommandStrategy.TRANSLATION).also {
            producingProvider.prepareMessageToSend(
                it.requestId,
                it,
                SenderType.PROCESSING_SENDER
            )
        }

    }

}

