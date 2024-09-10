package storage.trans.com.persistance.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

class MessageEntity(id: EntityID<Long>) : LongEntity(id) {

    var userId by MessageTable.userId
    var requestId by MessageTable.requestId
    var chatId by MessageTable.chatId
    var messageId by MessageTable.messageId
    var timestamp by MessageTable.timeStamp
    var messageValue by MessageTable.messageValue
    var messageResult by MessageTable.messageResult
    var translateResult by MessageTable.translateResult
    var lang by MessageTable.lang
    var status by MessageTable.status

    companion object : LongEntityClass<MessageEntity>(MessageTable)

}

object MessageTable : LongIdTable("MESSAGES", "ID") {

    val userId = reference("USER_ID", UserTable.userId)
    val requestId = varchar("REQUEST_ID", DEFAULT_VARCHAR_COLUMN_LENGTH)
    val chatId = long("CHAT_ID")
    val messageId = long("MESSAGE_ID")
    val timeStamp = datetime("TIMESTAMP")
    val messageValue = blob("MESSAGE_VALUE")
    val messageResult = blob("MESSAGE_RESULT").nullable()
    val translateResult = blob("TRANSLATE_RESULT").nullable()
    val lang = varchar("TRANSLATE_LANGUAGE", DEFAULT_VARCHAR_COLUMN_LENGTH).nullable()
    val status = enumeration("STATUS", MessageStatus::class).nullable()

}
