package com.trans.service.mapping

import com.trans.domain.MessageModel
import com.trans.persistanse.entity.MessageEntity
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.MediaContent
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.time.ZoneOffset
import java.util.*

fun MessageEntity.updateFields(messageModel: MessageModel): MessageModel {
    this.userId = messageModel.userId
    this.requestId = messageModel.requestId
    this.chatId = messageModel.chatId
    this.messageId = messageModel.messageId
    this.timestamp = messageModel.timeStampDate
    messageModel.messageValue?.let {
        this.messageValue = ExposedBlob(it)
    }
    messageModel.messageResult?.let {
        this.messageResult = ExposedBlob(it)
    }
    return messageModel.copy(
        id = this.id.value
    )
}

fun MessageEntity.toMessageModel(): MessageModel = MessageModel(
    this.id.value,
    this.userId,
    this.requestId,
    this.chatId,
    this.messageId,
    this.timestamp.toEpochSecond(ZoneOffset.UTC),
    this.messageValue?.bytes,
    this.messageResult?.bytes,
    this.status
)

fun MessageModel.toNewEntity(): MessageEntity {
    val messageModel = this
    return MessageEntity.new {
        userId = messageModel.userId
        requestId = messageModel.requestId
        chatId = messageModel.chatId
        timestamp = messageModel.timeStampDate
        messageModel.messageValue?.let {
            messageValue = ExposedBlob(it)
        }
        messageModel.messageResult?.let {
            messageResult = ExposedBlob(it)
        }
        status = messageModel.status
    }
}

fun ContentMessage<MediaContent>.toMessageModel(userId: Long, outFile: ByteArray): MessageModel = MessageModel(
    1L,
    userId,
    UUID.randomUUID().toString(),
    this.chat.id.chatId.long,
    this.messageId.long,
    System.currentTimeMillis(),
    outFile
    )
