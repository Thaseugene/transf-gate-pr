package com.trans.api

import com.trans.domain.EventRecordExecuteType
import com.trans.dto.EventRequest
import com.trans.plugins.KafkaService
import com.trans.service.EventService
import com.trans.service.mapping.toEventRecord
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class EventController(
    private val kafkaService: KafkaService,
    private val eventService: EventService
) {

    suspend fun getAllEventModels(call: ApplicationCall) {
        call.respond(HttpStatusCode.OK, eventService.findAll())
    }

    suspend fun getExistingEventModel(call: ApplicationCall) {
        call.respond(HttpStatusCode.OK, eventService.findEventById(call.parameters["id"]))
    }

    suspend fun deleteEventModel(call: ApplicationCall) {
        val request: EventRequest = call.receive()
        kafkaService.sendMessage(request.toEventRecord(EventRecordExecuteType.DELETE))
        call.respond(HttpStatusCode.OK)
    }

    suspend fun updateEventModel(call: ApplicationCall) {
        val request: EventRequest = call.receive()
        kafkaService.sendMessage(request.toEventRecord(EventRecordExecuteType.UPDATE))
        call.respond(HttpStatusCode.OK)
    }

    suspend fun createEventModel(call: ApplicationCall) {
        val request: EventRequest = call.receive()
        kafkaService.sendMessage(request.toEventRecord(EventRecordExecuteType.CREATE))
        call.respond(HttpStatusCode.OK)
    }

}
