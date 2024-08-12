package com.trans.service

import com.trans.domain.MessageModel
import com.trans.domain.UserModel
import com.trans.exception.RepositoryException
import com.trans.persistanse.MessageRepository
import com.trans.persistanse.UserRepository
import com.trans.service.mapping.toMessageModel
import com.trans.service.mapping.toNewUser
import com.trans.utils.extractId
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.MediaContent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface MessageService {

    fun processIncomingMessage(incomingMessage: ContentMessage<MediaContent>, outFile: ByteArray) : MessageModel?
    fun createEvent(event: MessageModel): MessageModel
    fun deleteEvent(id: String?)
    fun updateEvent(event: MessageModel): MessageModel
    fun findMessageById(id: Long): MessageModel
    fun findAll(): List<MessageModel>

}

class MessageServiceImpl(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository
) : MessageService {

    private val logger: Logger = LoggerFactory.getLogger(MessageService::class.java)

    override fun processIncomingMessage(incomingMessage: ContentMessage<MediaContent>, outFile: ByteArray): MessageModel? {
        val user: UserModel? = try {
            userRepository.findByUserId(incomingMessage.chat.id.chatId.long)
        } catch (ex: RepositoryException) {
            logger.error("User with id - ${incomingMessage.chat.id.chatId.long} not found. Creating new...")
            userRepository.save(incomingMessage.toNewUser())
        }
        user?.let {
           return messageRepository.save(incomingMessage.toMessageModel(it.userId, outFile))
        }
        return null
    }

    override fun createEvent(event: MessageModel): MessageModel {
        logger.info("Start creating process of event model - $event")
        return messageRepository.save(event)
    }

    override fun deleteEvent(id: String?) {
        logger.info("Start deleting process of event model with id - $id")
        messageRepository.delete(id.extractId())
    }

    override fun updateEvent(event: MessageModel): MessageModel {
        logger.info("Start updating process of event model - $event")
        return messageRepository.update(event)
    }

    override fun findMessageById(id: Long): MessageModel {
        return messageRepository.findById(id)
    }

    override fun findAll(): List<MessageModel> {
        logger.info("Start finding process for all event models")
        return messageRepository.findAll().map { it }
    }

}

