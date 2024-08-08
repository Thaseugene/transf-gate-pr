package com.trans.integration.transacription

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.trans.dto.PollingResponse
import com.trans.dto.TranscriptRequest
import com.trans.dto.TranscriptResponse
import com.trans.dto.UploadResponse
import com.trans.service.EventService
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.File

private const val UPLOAD_SERVICE_URL = "https://api.assemblyai.com/v2/upload"
private const val TRANSCRIPT_URL = "https://api.assemblyai.com/v2/transcript"
private const val RESULT_URL = "https://api.assemblyai.com/v2/transcript/%s"

class TranscriptionService(
    private val eventService: EventService
) {

    companion object {
        val client = HttpClient(CIO) {
            install(Logging) {
                level = LogLevel.INFO
            }
        }

        val objectMapper: ObjectMapper = with(jacksonObjectMapper()) {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    suspend fun tryToMakeTranscript(recordId: String) : String? {
//        val apiKey = System.getenv("apiKey") ?: "default_value"
        val apiKey = System.getenv("apiKey") ?: "default_value"
        val path = "Nice Talking with You.mp3"
        val bytesToAnalyze = eventService.findEventById(recordId).value
        uploadFile(path, apiKey, bytesToAnalyze);
        try {
            val uploadedFileUrl = uploadFile(path, apiKey, bytesToAnalyze)
            return getTranscript(uploadedFileUrl, apiKey)
        } catch (e: Exception) {
            e.printStackTrace()
            println(e.message)
        }
        return null;
    }

}

suspend fun uploadFile(path: String, apiKey: String, bytesToAnalyze: ByteArray? = null): String = withContext(IO) {
    val file = File(path)
    val response: HttpResponse = TranscriptionService.client.post(UPLOAD_SERVICE_URL) {
        contentType(ContentType.Application.OctetStream)
        header("Authorization", apiKey)
        setBody(bytesToAnalyze ?: file.readBytes())
    }
    val transcriptResponse = TranscriptionService.objectMapper.readValue(
        response.bodyAsText(),
        object : TypeReference<UploadResponse>() {})
    transcriptResponse.uploadUrl
}

suspend fun getTranscript(audioUrl: String, apiKey: String) = withContext(IO) {
    val response: HttpResponse = TranscriptionService.client.post(TRANSCRIPT_URL) {
        contentType(ContentType.Application.Json)
        header("Authorization", apiKey)
        setBody(TranscriptionService.objectMapper.writeValueAsString(TranscriptRequest(audioUrl)))
    }
    val transcriptResponse = TranscriptionService.objectMapper.readValue(
        response.bodyAsText(),
        object : TypeReference<TranscriptResponse>() {})
    val pollingEndpoint = RESULT_URL.format(transcriptResponse.id)
    checkResult(pollingEndpoint, apiKey).text
}

suspend fun checkResult(pollingEndpoint: String, apiKey: String): PollingResponse {
    val response: HttpResponse = TranscriptionService.client.get(pollingEndpoint) {
        bearerAuth(apiKey)
    }
    val pollingResponse = TranscriptionService.objectMapper.readValue(
        response.bodyAsText(),
        object : TypeReference<PollingResponse>() {})
    println("============${pollingResponse.status}=============")
    when (TranscriptionTaskStatus.valueOf(pollingResponse.status.uppercase())) {
        TranscriptionTaskStatus.ERROR -> throw RuntimeException("Task was ended with error")
        TranscriptionTaskStatus.QUEUED, TranscriptionTaskStatus.PROCESSING -> {
            delay(3000L)
            return checkResult(pollingEndpoint, apiKey)
        }
        TranscriptionTaskStatus.COMPLETED -> return pollingResponse
    }
    throw RuntimeException("Task ended without status")
}

fun main() {
    val apiKey = System.getenv("apiKey") ?: "default_value"
    val path = "Nice Talking with You.mp3"
    println(apiKey)
    GlobalScope.launch {
        try {
            val uploadedFileUrl = uploadFile(path, apiKey)
            getTranscript(uploadedFileUrl, apiKey)
        } catch (e: Exception) {
            e.printStackTrace()
            println(e.message)
        }
    }
    Thread.sleep(30000)
}
