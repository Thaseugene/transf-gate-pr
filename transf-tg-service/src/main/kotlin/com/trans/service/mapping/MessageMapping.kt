package com.trans.service.mapping

import com.trans.domain.MessageStatus
import com.trans.domain.ProcessingMessageRequest
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.MediaContent
import java.util.*


fun ContentMessage<MediaContent>.toProcessingMessage(messageValue: String): ProcessingMessageRequest =
    ProcessingMessageRequest(
        this.chat.id.chatId.long,
        UUID.randomUUID().toString(),
        this.chat.id.chatId.long,
        this.messageId.long,
        System.currentTimeMillis(),
        Base64.getEncoder().encode(messageValue.encodeToByteArray()),
        status = MessageStatus.NEW,
        userName = this.from?.username?.full,
        firstName = this.from?.firstName,
        lastName = this.from?.lastName
    )
