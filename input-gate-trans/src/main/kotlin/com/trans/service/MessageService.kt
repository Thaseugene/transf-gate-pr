package com.trans.service

import com.trans.service.mapping.toProcessingMessage
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.MediaContent
import org.koin.java.KoinJavaComponent.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.trans.messaging.MessagingProvider
import storage.trans.com.messaging.SenderType

interface MessageService {

    fun processTelegramMessage(incomingMessage: ContentMessage<MediaContent>, downloadFilePath: String)

}

class MessageServiceImpl : MessageService {

    private val messagingProvider by inject<MessagingProvider>(MessagingProvider::class.java)

    private val logger: Logger = LoggerFactory.getLogger(MessageService::class.java)

    override fun processTelegramMessage(
        incomingMessage: ContentMessage<MediaContent>,
        downloadFilePath: String
    ) {
        logger.info("Preparing incoming message from user -> ${incomingMessage.chat.id.chatId.long}")
        val processedMessage = incomingMessage.toProcessingMessage(downloadFilePath)
        messagingProvider.prepareMessageToSend(
            processedMessage.requestId,
            processedMessage,
            SenderType.PROCESSING_SENDER
        )
    }

}

