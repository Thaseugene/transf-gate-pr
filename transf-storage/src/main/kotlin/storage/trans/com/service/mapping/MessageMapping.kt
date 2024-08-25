package com.trans.service.mapping

import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import storage.trans.com.domain.MessageModel
import storage.trans.com.persistance.entity.MessageEntity
import java.time.ZoneOffset

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

