package com.trans.persistanse.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

class MessageEntity(id: EntityID<Long>) : LongEntity(id) {

    var user by MessageTable.user
    var requestId by MessageTable.requestId
    var chatId by MessageTable.chatId
    var messageId by MessageTable.messageId
    var timestamp by MessageTable.timeStamp
    var messageValue by MessageTable.messageValue
    var messageResult by MessageTable.messageResult
    var status by MessageTable.status

    companion object : LongEntityClass<MessageEntity>(MessageTable)

}

object MessageTable : LongIdTable("MESSAGES", "ID") {

    val user = reference("USER_ID", UserTable.userId)
    val requestId = varchar("REQUEST_ID", DEFAULT_VARCHAR_COLUMN_LENGTH)
    val chatId = varchar("CHAT_ID", DEFAULT_VARCHAR_COLUMN_LENGTH)
    val messageId = varchar("MESSAGE_ID", DEFAULT_VARCHAR_COLUMN_LENGTH)
    val timeStamp = datetime("TIMESTAMP")
    val messageValue = blob("MESSAGE_VALUE").nullable()
    val messageResult = blob("MESSAGE_RESULT").nullable()
    val status = enumeration("STATUS", MessageStatus::class).nullable()

}
