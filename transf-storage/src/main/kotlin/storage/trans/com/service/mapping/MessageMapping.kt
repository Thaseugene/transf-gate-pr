package storage.trans.com.service.mapping

import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import storage.trans.com.model.*
import storage.trans.com.exception.InnerException
import storage.trans.com.model.request.TelegramMessageRequest
import storage.trans.com.model.request.TranscriptMessageRequest
import storage.trans.com.model.request.TranslateMessageRequest
import storage.trans.com.model.response.TelegramMessageResponse
import storage.trans.com.model.response.TranscriptionMessageResponse
import storage.trans.com.model.response.TranslateMessageResponse
import storage.trans.com.persistance.entity.MessageEntity
import storage.trans.com.persistance.entity.MessageStatus
import java.time.ZoneOffset
import java.util.*

fun MessageEntity.updateFields(messageModel: MessageModel): MessageModel {
    this.userId = messageModel.userId
    this.requestId = messageModel.requestId
    this.chatId = messageModel.chatId
    this.messageId = messageModel.messageId
    this.timestamp = messageModel.timeStampDate
    this.messageValue = ExposedBlob(messageModel.messageValue)
    this.status = messageModel.status
    messageModel.messageResult?.let {
        this.messageResult = ExposedBlob(it)
    }
    messageModel.translateResult?.let {
        this.translateResult = ExposedBlob(it)
    }
    this.lang = messageModel.lang
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
    this.messageValue.bytes,
    this.messageResult?.bytes,
    this.translateResult?.bytes,
    this.lang,
    this.status
)

fun MessageModel.updateTranscriptFields(incomingMessage: TranscriptMessageRequest): MessageModel {
    this.timeStamp = System.currentTimeMillis()
    this.messageResult = Base64.getEncoder().encode(incomingMessage.messageResult.toByteArray())
    this.status = incomingMessage.status
    return this
}

fun MessageModel.updateTranslateFields(incomingMessage: TranslateMessageRequest): MessageModel {
    this.timeStamp = System.currentTimeMillis()
    this.translateResult = incomingMessage.translatedValue
    this.status = incomingMessage.status
    this.lang = incomingMessage.lang
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
    lang = this.lang,
    status = MessageStatus.NEW
)

fun TelegramMessageRequest.toUserModel() = UserModel(
    0L,
    this.userId,
    this.userName,
    this.firstName,
    this.lastName
)

fun MessageModel.toTranscriptResponse() = TranscriptionMessageResponse(
    this.requestId,
    this.messageValue
)

fun MessageModel.toTelegramResponse(result: String) = TelegramMessageResponse(
    this.requestId,
    this.chatId,
    this.messageId,
    result,
    status = this.status ?: MessageStatus.ERROR
)

fun MessageModel.toTelegramTranslateResponse(result: String) = TelegramMessageResponse(
    this.requestId,
    this.chatId,
    this.messageId,
    translatedResult = result,
    status = this.status ?: MessageStatus.ERROR
)

fun TelegramMessageRequest.toTranslateMessageResponse(valueToTranslate: ByteArray) = TranslateMessageResponse(
    this.requestId,
    valueToTranslate,
    this.lang ?: throw InnerException("Language isn't presented in incoming message")
)

fun ByteArray?.decode(): String {
    return if (this == null)
        throw InnerException("Couldn't decode value from Base64") else
        Base64.getDecoder().decode(this).decodeToString()
}


