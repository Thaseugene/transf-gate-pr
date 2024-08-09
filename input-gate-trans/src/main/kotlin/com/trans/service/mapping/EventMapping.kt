package com.trans.service.mapping

import com.trans.domain.MessageModel
import com.trans.domain.EventRecord
import com.trans.domain.EventRecordExecuteType
import com.trans.dto.EventRequest
import com.trans.dto.EventResponse
import com.trans.persistanse.entity.MessageEntity
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.time.ZoneId
import java.util.UUID

fun MessageEntity.updateFields(messageModel: MessageModel): MessageModel {
    this.clientId = messageModel.clientId
    this.topicName = messageModel.topicName
    this.requestId = messageModel.requestId
    this.description = messageModel.description
    this.timeStamp = messageModel.timeStampDate
    this.eventName = messageModel.eventName
    messageModel.value?.let {
        this.value = ExposedBlob(it)
    }
    return messageModel.copy(
        id = this.id.value
    )
}

fun MessageEntity.toEventModel(): MessageModel = MessageModel(
    this.id.value,
    this.clientId,
    this.topicName,
    this.requestId,
    this.description,
    this.timeStamp.toEpochSecond(ZoneId.systemDefault().rules.getOffset(this.timeStamp)),
    this.eventName,
    this.value.bytes
)

fun EventResponse.toEventModel(): MessageModel = MessageModel (
    this.id,
    this.clientId,
    this.topicName,
    UUID.randomUUID().toString(),
    this.description,
    this.timeStamp,
    this.eventName,
    this.value
)

fun MessageModel.toEventDto() = EventResponse(
    this.id,
    this.clientId,
    this.topicName,
    this.requestId,
    this.description,
    this.timeStamp,
    this.eventName,
    this.value
)

fun EventRequest.toEventRecord(eventType: EventRecordExecuteType) = EventRecord(
    this.id,
    this.clientId,
    this.topicName,
    UUID.randomUUID().toString(),
    this.description,
    this.timeStamp,
    this.eventName,
    this.value ?: byteArrayOf(),
    eventType
)

fun EventRecord.toEventModel() = MessageModel(
    this.id,
    this.clientId,
    this.topicName,
    this.requestId,
    this.description,
    this.timeStamp,
    this.eventName,
    this.value
)
