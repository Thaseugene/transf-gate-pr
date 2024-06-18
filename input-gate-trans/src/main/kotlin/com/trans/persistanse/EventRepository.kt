package com.trans.persistanse

import com.trans.domain.EventModel
import com.trans.exception.ExpCode
import com.trans.exception.RepositoryException
import com.trans.persistanse.entity.EventEntity
import com.trans.service.mapping.updateFields
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction

interface EventRepository {

    fun save(eventModel: EventModel): EventModel
    fun delete(id: Long)
    fun update(test: EventModel): EventModel
    fun findById(id: Long): EventModel
    fun findAll(): List<EventModel>

}

class EventRepositoryImpl : EventRepository {

    override fun save(eventModel: EventModel): EventModel = transaction {
        val createdEntity = EventEntity.new {
            clientId = eventModel.clientId
            topicName = eventModel.topicName
            requestId = eventModel.requestId
            description = eventModel.description
            timeStamp = eventModel.timeStampDate
            eventName = eventModel.eventName
            eventModel.value?.let {
                value = ExposedBlob(it)
            }
        }
        eventModel.copy(
            id = createdEntity.id.value
        )
    }

    override fun delete(id: Long) {
        val existing = findExistingById(id) ?: throw RepositoryException(ExpCode.NOT_FOUND, "Event with id = $id doesn't exists")
    }

    override fun update(eventModel: EventModel): EventModel = transaction {
        val existing = findExistingById(eventModel.id) ?: throw RepositoryException(ExpCode.NOT_FOUND, "Event with id = ${eventModel.id} doesn't exists")
        existing.updateFields(eventModel)
    }

    override fun findById(id: Long): EventModel {
        findExistingById(id)?.toEvent
    }

    override fun findAll(): List<EventModel> {
        TODO("Not yet implemented")
    }

    private fun findExistingById(id: Long): EventEntity? = EventEntity.findById(id)

}
