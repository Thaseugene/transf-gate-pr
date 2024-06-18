package com.trans.persistanse.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

class EventEntity(id: EntityID<Long>) : LongEntity(id) {
    var clientId by EventTable.clientId
    var topicName by EventTable.topicName
    var requestId by EventTable.requestId
    var description by EventTable.description
    var timeStamp by EventTable.timeStamp
    var eventName by EventTable.eventName
    var value by EventTable.value

    companion object : LongEntityClass<EventEntity>(EventTable)
}

object EventTable : LongIdTable("EVENT", "ID") {
    val clientId = varchar("NAME", DEFAULT_VARCHAR_COLUMN_LENGTH)
    val topicName = varchar("NAME", DEFAULT_VARCHAR_COLUMN_LENGTH)
    val requestId = varchar("NAME", DEFAULT_VARCHAR_COLUMN_LENGTH)
    val description = varchar("DESCRIPTION", DEFAULT_VARCHAR_COLUMN_LENGTH)
    val timeStamp = datetime("UPDATE_TIME")
    val eventName = varchar("EVENT_NAME", DEFAULT_VARCHAR_COLUMN_LENGTH)
    val value = blob("EVENT_VALUE")
}
