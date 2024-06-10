package com.trans.api

import com.trans.service.TestService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class TestController(
    private val testService: TestService
) {

    suspend fun getExistingTestModel(call: ApplicationCall) {
        call.respond(HttpStatusCode.OK, testService.findTestById(call.parameters["id"]))
    }

    suspend fun deleteTestModel(call: ApplicationCall) {
        call.respond(HttpStatusCode.OK, testService.deleteTest(call.parameters["id"]))
    }

    suspend fun updateTestModel(call: ApplicationCall) {
        call.respond(HttpStatusCode.OK, testService.updateTest(call.receive()))
    }

    suspend fun createTestModel(call: ApplicationCall) {
        call.respond(HttpStatusCode.OK, testService.createTest(call.receive()))
    }

    suspend fun getAllTestModels(call: ApplicationCall) {
        call.respond(HttpStatusCode.OK, testService.findAll())
    }

}
