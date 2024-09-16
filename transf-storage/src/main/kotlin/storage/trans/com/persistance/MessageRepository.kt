package storage.trans.com.persistance

import storage.trans.com.exception.RepositoryException
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import storage.trans.com.model.MessageModel
import storage.trans.com.model.TranslateModel
import storage.trans.com.persistance.entity.MessageEntity
import storage.trans.com.persistance.entity.MessageTable
import storage.trans.com.persistance.entity.TranslateEntity
import storage.trans.com.service.mapping.toMessageModel
import storage.trans.com.service.mapping.updateFields

interface MessageRepository {

    fun save(messageModel: MessageModel): MessageModel
    fun saveTranslation(translateModel: TranslateModel, requestId: String): MessageModel
    fun findByRequestId(requestId: String): MessageModel?
    fun update(messageModel: MessageModel): MessageModel
    fun findById(id: Long): MessageModel

}

class MessageRepositoryImpl : MessageRepository {

    override fun save(messageModel: MessageModel): MessageModel = transaction {
        val createdEntity = MessageEntity.new {
            userId = messageModel.userId
            requestId = messageModel.requestId
            chatId = messageModel.chatId
            messageId = messageModel.messageId
            timestamp = messageModel.timeStampDate
            messageValue = ExposedBlob(messageModel.messageValue)
            messageModel.messageResult?.let {
                messageResult = ExposedBlob(it)
            }
            status = messageModel.status
        }
        messageModel.copy(
            id = createdEntity.id.value
        )
    }

    override fun saveTranslation(translateModel: TranslateModel, requestId: String): MessageModel = transaction {
        findBy(MessageTable.requestId, requestId).first()?.let {
            TranslateEntity.new {
                message = it
                lang = translateModel.lang
                translateModel.translateResult?.let { result -> translateResult = ExposedBlob(result) }
            }
            it.toMessageModel()
        } ?: throw RepositoryException("Message with requestId $requestId not found")
    }

    override fun findByRequestId(requestId: String): MessageModel? = transaction {
        val messageList = findBy(MessageTable.requestId, requestId)
        messageList.first()?.toMessageModel()
    }

    override fun update(messageModel: MessageModel): MessageModel = transaction {
        val existing = findExistingById(messageModel.id) ?:
        throw RepositoryException("Message with id = ${messageModel.id} doesn't exists")
        existing.updateFields(messageModel)
    }

    override fun findById(id: Long): MessageModel = transaction {
        findExistingById(id)?.toMessageModel() ?:
        throw RepositoryException("Message with id = $id doesn't exists")
    }

    private fun findExistingById(id: Long): MessageEntity? = MessageEntity.findById(id)

    private fun <T> findBy(column: Column<T>, value: T): List<MessageEntity?> = transaction {
        MessageTable.select { column eq value }
            .mapNotNull { toMessageEntity(it) }
            .toList()
    }

    private fun toMessageEntity(row: ResultRow): MessageEntity {
        return MessageEntity.wrap(row[MessageTable.id], row)
    }

}
