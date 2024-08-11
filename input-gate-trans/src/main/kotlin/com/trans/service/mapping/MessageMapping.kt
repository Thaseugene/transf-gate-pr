package com.trans.service.mapping

import com.trans.domain.MessageModel
import com.trans.domain.UserModel
import com.trans.persistanse.entity.MessageEntity
import com.trans.persistanse.entity.UserEntity
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.time.ZoneOffset

fun MessageEntity.updateFields(messageModel: MessageModel): MessageModel {
    this.user = messageModel.user
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
    this.user,
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
        user = messageModel.user
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
