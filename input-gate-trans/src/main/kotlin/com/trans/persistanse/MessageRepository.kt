package com.trans.persistanse

import com.trans.domain.MessageModel
import com.trans.exception.ExpCode
import com.trans.exception.RepositoryException
import com.trans.persistanse.entity.MessageEntity
import com.trans.persistanse.entity.MessageTable
import com.trans.service.mapping.toEventModel
import com.trans.service.mapping.updateFields
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction

interface MessageRepository {

    fun save(event: MessageModel): MessageModel
    fun delete(id: Long)
    fun update(event: MessageModel): MessageModel
    fun findById(id: Long): MessageModel
    fun findAll(): List<MessageModel>

}

class MessageRepositoryImpl : MessageRepository {

    override fun save(messageModel: MessageModel): MessageModel = transaction {
        val createdEntity = MessageEntity.new {
            user = messageModel.user.id
            requestId = messageModel.requestId
            chatId = messageModel.chatId
            timestamp = messageModel.timeStampDate
            messageModel.messageValue?.let {
                messageValue = ExposedBlob(it)
            }
            messageModel.messageResult?.let {
                messageResult = ExposedBlob(it)
            }
            status = messageModel.status
        }
        messageModel.copy(
            id = createdEntity.id.value
        )
    }

    override fun delete(id: Long) {
        val existing = findExistingById(id) ?: throw RepositoryException(ExpCode.NOT_FOUND, "Message with id = $id doesn't exists")
        existing.delete()
    }

    override fun update(event: MessageModel): MessageModel = transaction {
        val existing = findExistingById(event.id) ?: throw RepositoryException(ExpCode.NOT_FOUND, "Event with id = ${event.id} doesn't exists")
        existing.updateFields(event)
    }

    override fun findById(id: Long): MessageModel = transaction {
        findExistingById(id)?.toEventModel() ?: throw RepositoryException(ExpCode.NOT_FOUND, "Event with id = $id doesn't exists")
    }

    override fun findAll(): List<MessageModel> = transaction {
        MessageEntity.all().map { it.toEventModel() }
    }

    private fun findExistingById(id: Long): MessageEntity? = MessageEntity.findById(id)

}
