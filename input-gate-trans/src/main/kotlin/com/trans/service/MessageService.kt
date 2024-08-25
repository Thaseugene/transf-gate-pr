package com.trans.service

import com.trans.service.mapping.toProcessingMessage
import dev.inmo.tgbotapi.types.files.PathedFile
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.MediaContent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import storage.trans.com.messaging.MessagingProvider
import storage.trans.com.messaging.SenderType

interface MessageService {

    fun processTelegramMessage(incomingMessage: ContentMessage<MediaContent>, downloadFilePath: String)


}

class MessageServiceImpl(
    private val messagingProvider: MessagingProvider
) : MessageService {

    private val logger: Logger = LoggerFactory.getLogger(MessageService::class.java)

    override fun processTelegramMessage(incomingMessage: ContentMessage<MediaContent>,
                                        downloadFilePath: String) {
        logger.info("Preparing incoming message from user -> ${incomingMessage.chat.id.chatId.long}")
        messagingProvider.prepareMessageToSend(
            incomingMessage.toProcessingMessage(downloadFilePath),
            SenderType.PROCESSING_SENDER
        )
    }


}

