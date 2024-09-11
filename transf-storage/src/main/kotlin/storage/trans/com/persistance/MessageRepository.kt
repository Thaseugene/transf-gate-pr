package storage.trans.com.persistance

import storage.trans.com.exception.ExpCode
import com.trans.exception.RepositoryException
import storage.trans.com.service.mapping.toMessageModel
import storage.trans.com.service.mapping.updateFields
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import storage.trans.com.model.MessageModel
import storage.trans.com.persistance.entity.MessageEntity
import storage.trans.com.persistance.entity.MessageTable

interface MessageRepository {

    fun save(messageModel: MessageModel): MessageModel
    fun findByMessageId(messageId: Long): MessageModel
    fun findByChatId(chatId: Long): List<MessageModel>
    fun findByUserId(userId: Long): List<MessageModel>
    fun findByRequestId(requestId: String): MessageModel?
    fun delete(id: Long)
    fun update(messageModel: MessageModel): MessageModel
    fun findById(id: Long): MessageModel
    fun findAll(): List<MessageModel>

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

    override fun findByMessageId(messageId: Long): MessageModel = transaction {
        val messageList = findBy(MessageTable.messageId, messageId)
        if (messageList.isEmpty()) {
            throw RepositoryException(ExpCode.NOT_FOUND, "Message with messageId = $messageId doesn't exists")
        }
        messageList.first()?.toMessageModel() ?: throw RepositoryException(ExpCode.NOT_FOUND, "Message with messageId = $messageId doesn't exists")
    }

    override fun findByChatId(chatId: Long): List<MessageModel> = transaction {
        val messageList = findBy(MessageTable.chatId, chatId).filterNotNull().map { it.toMessageModel() }
        if (messageList.isEmpty()) {
            throw RepositoryException(ExpCode.NOT_FOUND, "Messages with chatId = $chatId doesn't exists")
        }
        messageList
    }

    override fun findByUserId(userId: Long): List<MessageModel> = transaction {
        val messageList = findBy(MessageTable.userId, userId).filterNotNull().map { it.toMessageModel() }
        if (messageList.isEmpty()) {
            throw RepositoryException(ExpCode.NOT_FOUND, "Messages with userId = $userId doesn't exists")
        }
        messageList
    }

    override fun findByRequestId(requestId: String): MessageModel? = transaction {
        val messageList = findBy(MessageTable.requestId, requestId)
        messageList.first()?.toMessageModel()
    }

    override fun delete(id: Long) = transaction {
        val existing = findExistingById(id) ?: throw RepositoryException(ExpCode.NOT_FOUND, "Message with id = $id doesn't exists")
        existing.delete()
    }

    override fun update(messageModel: MessageModel): MessageModel = transaction {
        val existing = findExistingById(messageModel.id) ?: throw RepositoryException(ExpCode.NOT_FOUND, "Message with id = ${messageModel.id} doesn't exists")
        existing.updateFields(messageModel)
    }

    override fun findById(id: Long): MessageModel = transaction {
        findExistingById(id)?.toMessageModel() ?: throw RepositoryException(ExpCode.NOT_FOUND, "Message with id = $id doesn't exists")
    }

    override fun findAll(): List<MessageModel> = transaction {
        MessageEntity.all().map { it.toMessageModel() }
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
