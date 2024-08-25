package com.trans.service.mapping

import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import storage.trans.com.domain.*
import storage.trans.com.persistance.entity.MessageEntity
import storage.trans.com.persistance.entity.MessageStatus
import java.time.ZoneOffset

fun MessageEntity.updateFields(messageModel: MessageModel): MessageModel {
    this.userId = messageModel.userId
    this.requestId = messageModel.requestId
    this.chatId = messageModel.chatId
    this.messageId = messageModel.messageId
    this.timestamp = messageModel.timeStampDate
    this.messageValue = messageModel.messageValue
    messageModel.messageResult?.let {
        this.messageResult = ExposedBlob(it)
    }
    return messageModel.copy(
        id = this.id.value
    )
}

fun MessageEntity.toMessageModel() = MessageModel(
    this.id.value,
    this.userId,
    this.requestId,
    this.chatId,
    this.messageId,
    this.timestamp.toEpochSecond(ZoneOffset.UTC),
    this.messageValue,
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
        messageValue = messageModel.messageValue
        messageModel.messageResult?.let {
            messageResult = ExposedBlob(it)
        }
        status = messageModel.status
    }
}

fun MessageModel.updateTranscriptFields(incomingMessage: TranscriptMessageRequest): MessageModel {
    this.messageResult = incomingMessage.messageResult
    this.status = incomingMessage.status
    return this
}

fun TelegramMessageRequest.toMessageModel() = MessageModel(
    0L,
    this.userId,
    this.requestId,
    this.chatId,
    this.messageId,
    this.timeStamp,
    this.messageValue,
    this.messageResult,
    MessageStatus.NEW
)

fun TelegramMessageRequest.toUserModel() = UserModel(
    0L,
    this.userId,
    this.userName,
    this.firstName,
    this.lastName
)

fun MessageModel.toTranscriptResponse() =  TranscriptionMessageResponse(
    this.requestId,
    this.messageValue
)

fun MessageModel.toTelegramResponse(result: String) = TelegramMessageResponse(
    this.requestId,
    this.chatId,
    this.messageId,
    result
)

