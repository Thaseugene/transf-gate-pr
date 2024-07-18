package com.trans.persistanse

import com.trans.domain.EventModel
import com.trans.exception.ExpCode
import com.trans.exception.RepositoryException
import com.trans.persistanse.entity.EventEntity
import com.trans.service.mapping.toEventModel
import com.trans.service.mapping.updateFields
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction

interface EventRepository {

    fun save(event: EventModel): EventModel
    fun delete(id: Long)
    fun update(event: EventModel): EventModel
    fun findById(id: Long): EventModel
    fun findAll(): List<EventModel>

}

class EventRepositoryImpl : EventRepository {

    override fun save(event: EventModel): EventModel = transaction {
        val createdEntity = EventEntity.new {
            clientId = event.clientId
            topicName = event.topicName
            requestId = event.requestId
            description = event.description
            timeStamp = event.timeStampDate
            eventName = event.eventName
            event.value?.let {
                value = ExposedBlob(it)
            }
        }
        event.copy(
            id = createdEntity.id.value
        )
    }

    override fun delete(id: Long) {
        val existing =
            findExistingById(id) ?: throw RepositoryException(
                ExpCode.NOT_FOUND,
                "Event with id = $id doesn't exists")
        existing.delete()
    }

    override fun update(event: EventModel): EventModel = transaction {
        val existing = findExistingById(event.id) ?: throw RepositoryException(
            ExpCode.NOT_FOUND,
            "Event with id = ${event.id} doesn't exists"
        )
        existing.updateFields(event)
    }

    override fun findById(id: Long): EventModel = transaction {
        findExistingById(id)?.toEventModel() ?: throw RepositoryException(
            ExpCode.NOT_FOUND,
            "Event with id = $id doesn't exists"
        )
    }

    override fun findAll(): List<EventModel> = transaction {
        EventEntity.all().map { it.toEventModel() }
    }

    private fun findExistingById(id: Long): EventEntity? = EventEntity.findById(id)

}
