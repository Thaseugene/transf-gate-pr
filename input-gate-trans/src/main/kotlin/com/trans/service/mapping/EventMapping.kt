package com.trans.service.mapping

import com.trans.domain.EventModel
import com.trans.domain.EventRecord
import com.trans.domain.EventRecordExecuteType
import com.trans.dto.EventRequest
import com.trans.dto.EventResponse
import com.trans.persistanse.entity.EventEntity
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.time.ZoneId
import java.util.UUID

fun EventEntity.updateFields(eventModel: EventModel): EventModel {
    this.clientId = eventModel.clientId
    this.topicName = eventModel.topicName
    this.requestId = eventModel.requestId
    this.description = eventModel.description
    this.timeStamp = eventModel.timeStampDate
    this.eventName = eventModel.eventName
    eventModel.value?.let {
        this.value = ExposedBlob(it)
    }
    return eventModel.copy(
        id = this.id.value
    )
}

fun EventEntity.toEventModel(): EventModel = EventModel(
    this.id.value,
    this.clientId,
    this.topicName,
    this.requestId,
    this.description,
    this.timeStamp.toEpochSecond(ZoneId.systemDefault().rules.getOffset(this.timeStamp)),
    this.eventName,
    this.value.bytes
)

fun EventResponse.toEventModel(): EventModel = EventModel (
    this.id,
    this.clientId,
    this.topicName,
    UUID.randomUUID().toString(),
    this.description,
    this.timeStamp,
    this.eventName,
    this.value
)

fun EventModel.toEventDto() = EventResponse(
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

fun EventRequest.toEventModel() = EventModel(
    this.id,
    this.clientId,
    this.topicName,
    UUID.randomUUID().toString(),
    this.description,
    this.timeStamp,
    this.eventName,
    this.value
)

fun EventResponse.toEventRecord(): EventRecord = EventRecord (
    this.id,
    this.clientId,
    this.topicName,
    UUID.randomUUID().toString(),
    this.description,
    this.timeStamp,
    this.eventName,
    byteArrayOf(),
    EventRecordExecuteType.CREATE
)

fun EventRecord.toEventModel() = EventModel(
    this.id,
    this.clientId,
    this.topicName,
    this.requestId,
    this.description,
    this.timeStamp,
    this.eventName,
    this.value
)
