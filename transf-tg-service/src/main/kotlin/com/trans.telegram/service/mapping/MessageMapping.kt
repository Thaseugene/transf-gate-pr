package com.trans.telegram.service.mapping

import com.trans.telegram.model.MessageStatus
import com.transf.kafka.messaging.common.model.request.CommandStrategy
import com.transf.kafka.messaging.common.model.request.TelegramMessageRequest
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.MediaContent
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import java.util.*


fun ContentMessage<MediaContent>.toProcessingMessage(
    messageValue: String,
    commandStrategy: CommandStrategy
) =
    TelegramMessageRequest(
        commandStrategy,
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

fun MessageDataCallbackQuery.toProcessingMessage(
    requestId: String,
    lang: String,
    commandStrategy: CommandStrategy
) = TelegramMessageRequest(
    commandStrategy,
    this.from.id.chatId.long,
    requestId,
    this.from.id.chatId.long,
    this.message.messageId.long,
    System.currentTimeMillis(),
    "XXX".encodeToByteArray(),
    lang = lang
)
