package com.trans.service.mapping

import com.trans.domain.EventModel
import com.trans.persistanse.entity.EventEntity
import org.jetbrains.exposed.sql.statements.api.ExposedBlob

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

)
