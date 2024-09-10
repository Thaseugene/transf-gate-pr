package com.trans.telegram.service.mapping

import com.trans.telegram.domain.MessageStatus
import com.trans.telegram.domain.ProcessingMessageRequest
import com.trans.telegram.domain.ProcessingMessageResponse
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.MediaContent
import java.util.*
import kotlin.reflect.full.memberFunctions


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

fun ProcessingMessageResponse.validate(): Boolean {
    val components = this::class.memberFunctions
        .filter { it.name.startsWith("component") && it.parameters.size == 1 }
        .sortedBy { it.name }

    return components.all { component ->
        val value = component.call(this)
        value != null
    }
}
