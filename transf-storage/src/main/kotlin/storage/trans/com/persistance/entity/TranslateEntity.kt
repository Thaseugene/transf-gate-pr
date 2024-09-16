package storage.trans.com.persistance.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

class TranslateEntity(id: EntityID<Long>) : LongEntity(id) {

    var message by MessageEntity referencedOn TranslateTable.message
    var lang by TranslateTable.lang
    var translateResult by TranslateTable.translateResult

    companion object : LongEntityClass<TranslateEntity>(TranslateTable)
}

object TranslateTable : LongIdTable("TRANSLATES", "ID") {

    val message = reference("MESSAGE_ID", MessageTable.messageId)
    val translateResult = blob("TRANSLATE_RESULT").nullable()
    val lang = varchar("TRANSLATE_LANGUAGE", DEFAULT_VARCHAR_COLUMN_LENGTH).nullable()

}
