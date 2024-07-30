package com.trans.integration.transacription

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.cio.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class UploadResponse(val upload_url: String)

@Serializable
data class TranscriptRequest(val audio_url: String)

@Serializable
data class TranscriptResponse(val id: String)

@Serializable
data class PollingResponse(val status: String, val text: String? = null, val error: String? = null)

class TranscriptionService() {
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
}

private const val UPLOAD_SERVICE_URL = "https://api.assemblyai.com/v2/upload"
private const val TRANSCRIPT_URL = "https://api.assemblyai.com/v2/transcript"
private const val RESULT_URL = "https://api.assemblyai.com/v2/transcript/%s"

suspend fun uploadFile(path: String, apiKey: String): String = withContext(IO) {
    val file = File(path)
    val response: HttpResponse = TranscriptionService.client.post(UPLOAD_SERVICE_URL) {
        contentType(ContentType.Application.OctetStream)
        bearerAuth(apiKey)
        setBody(file.readChannel())
    }
    val transcriptResponse = TranscriptionService.objectMapper.readValue(
        response.bodyAsText(),
        object : TypeReference<UploadResponse>() {})
    transcriptResponse.upload_url
}

suspend fun getTranscript(audioUrl: String, apiKey: String) = withContext(IO) {
    val response: HttpResponse = TranscriptionService.client.post(TRANSCRIPT_URL) {
        contentType(ContentType.Application.Json)
        bearerAuth(apiKey)
        setBody(TranscriptionService.objectMapper.writeValueAsString(TranscriptRequest(audioUrl)))
    }
    val transcriptResponse = TranscriptionService.objectMapper.readValue(
        response.bodyAsText(),
        object : TypeReference<TranscriptResponse>() {})
    val pollingEndpoint = RESULT_URL.format(transcriptResponse.id)
    println(checkResult(pollingEndpoint, apiKey).text)
}

suspend fun checkResult(pollingEndpoint: String, apiKey: String): PollingResponse {
    val response: HttpResponse = TranscriptionService.client.get(pollingEndpoint) {
        bearerAuth(apiKey)
    }
    val pollingResponse = TranscriptionService.objectMapper.readValue(
        response.bodyAsText(),
        object : TypeReference<PollingResponse>() {})

    when (getStatus(pollingResponse)) {
        TranscriptionTaskStatus.ERROR -> throw RuntimeException("Task was ended with error")
        TranscriptionTaskStatus.PROCESSING -> {
            delay(3000L)
            return checkResult(pollingEndpoint, apiKey)
        }

        TranscriptionTaskStatus.QUEUED -> {
            delay(3000L)
            return checkResult(pollingEndpoint, apiKey)
        }

        TranscriptionTaskStatus.COMPLETED -> return pollingResponse
    }
    throw RuntimeException("Task ended without status")
}

suspend fun getStatus(pollingResponse: PollingResponse): TranscriptionTaskStatus {
    println(pollingResponse.status)
    return TranscriptionTaskStatus.valueOf(pollingResponse.status.uppercase())
}

fun main() {
    val apiKey = "c4dcb2208a1c4c8088b4b7787f4f7cfe"
    val path = "Nice Talking with You.mp3"
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
