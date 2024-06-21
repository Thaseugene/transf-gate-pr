package com.trans.service

import com.trans.domain.EventModel
import com.trans.domain.EventRecord
import com.trans.dto.EventResponse
import com.trans.persistanse.EventRepository
import com.trans.service.mapping.toEventDto
import com.trans.service.mapping.toEventModel
import com.trans.utils.extractId
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface EventService {

    fun createEvent(event: EventModel): EventResponse
    fun deleteEvent(id: String?)
    fun updateEvent(event: EventModel): EventResponse
    fun findEventById(id: String?): EventResponse
    fun findAll(): List<EventResponse>

}

class EventServiceImpl(
    private val eventRepository: EventRepository
): EventService {

    private val logger: Logger = LoggerFactory.getLogger(EventService::class.java)

    override fun createEvent(event: EventModel): EventResponse {
        logger.info("Start creating process of event model - $event")
        return eventRepository.save(event).toEventDto()
    }

    override fun deleteEvent(id: String?) {
        logger.info("Start deleting process of event model with id - $id")
        eventRepository.delete(id.extractId())
    }

    override fun updateEvent(event: EventModel): EventResponse {
        logger.info("Start updating process of event model - $event")
        return eventRepository.update(event).toEventDto()
    }

    override fun findEventById(id: String?): EventResponse {
        return eventRepository.findById(id.extractId()).toEventDto()
    }

    override fun findAll(): List<EventResponse> {
        logger.info("Start finding process for all event models")
        return eventRepository.findAll().map { it.toEventDto() }
    }

}

