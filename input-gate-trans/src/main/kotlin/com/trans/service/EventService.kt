package com.trans.service

import com.trans.domain.MessageModel
import com.trans.persistanse.MessageRepository
import com.trans.service.mapping.toEventDto
import com.trans.utils.extractId
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface EventService {

    fun createEvent(event: MessageModel): MessageModel
    fun deleteEvent(id: String?)
    fun updateEvent(event: MessageModel): MessageModel
    fun findEventById(id: String?): MessageModel
    fun findAll(): List<MessageModel>

}

class EventServiceImpl(
    private val messageRepository: MessageRepository
): EventService {

    private val logger: Logger = LoggerFactory.getLogger(EventService::class.java)

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

    override fun findEventById(id: String?): MessageModel {
        return messageRepository.findById(id.extractId())
    }

    override fun findAll(): List<MessageModel> {
        logger.info("Start finding process for all event models")
        return messageRepository.findAll().map { it }
    }

}

