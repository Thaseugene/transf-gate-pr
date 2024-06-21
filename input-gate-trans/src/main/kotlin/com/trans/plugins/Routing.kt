package com.trans.plugins

import com.trans.api.EventController
import com.trans.api.TestController
import com.trans.dto.Error
//import com.trans.service.StreamingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val testController by inject<TestController>()
    val eventController by inject<EventController>()

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            logError(call, cause)
            call.respond(
                HttpStatusCode.BadRequest,
                Error(
                    HttpStatusCode.BadRequest.value,
                    cause.message
                )
            )
        }
    }

    install(Resources)

    routing {
        route("/api/test") {
            get("/all") {
                testController.getAllTestModels(call)
            }
            get("{id}") {
                testController.getExistingTestModel(call)
            }
            delete("{id}") {
                testController.deleteTestModel(call)
            }
            post {
                testController.createTestModel(call)
            }
            put {
                testController.updateTestModel(call)
            }
        }
        route("api/event") {
            get("/all") {
                eventController.getAllEventModels(call)
            }
            get("{id}") {
                eventController.getExistingEventModel(call)
            }
            delete("{id}") {
                eventController.deleteEventModel(call)
            }
            post {
                eventController.createEventModel(call)
            }
            put {
                eventController.updateEventModel(call)
            }
        }
    }
}

