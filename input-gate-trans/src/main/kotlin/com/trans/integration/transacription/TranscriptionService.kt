package com.trans.integration.transacription

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.trans.dto.PollingResponse
import com.trans.dto.TranscriptRequest
import com.trans.dto.TranscriptResponse
import com.trans.dto.UploadResponse
import com.trans.integration.transacription.TranscriptionService.Companion.client
import com.trans.service.MessageService
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


private const val UPLOAD_SERVICE_URL = "https://api.assemblyai.com/v2/upload"
private const val TRANSCRIPT_URL = "https://api.assemblyai.com/v2/transcript"
private const val RESULT_URL = "https://api.assemblyai.com/v2/transcript/%s"

class TranscriptionService(
    private val messageService: MessageService
) {

    companion object {
        val client = HttpClient(OkHttp) {
            install(Logging) {
                level = LogLevel.ALL
            }
            engine {
                proxy = ProxyBuilder.socks("148.113.162.23", 63309)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 60000
                socketTimeoutMillis = 60000
                connectTimeoutMillis = 60000
            }
        }

        val objectMapper: ObjectMapper = with(jacksonObjectMapper()) {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    suspend fun tryToMakeTranscript(recordId: Long): String? {
        val apiKey = System.getenv("apiKey") ?: "default_value"
        val path = "Record (online-voice-recorder.com).mp3"
        val bytesToAnalyze = null
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
        setBody(TranscriptionService.objectMapper.writeValueAsString(TranscriptRequest(audioUrl, true)))
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

suspend fun testGptCheck() {
    val file = File("Record (online-voice-recorder.com).mp3")
    println(client.engine.config.proxy?.address())
    val response: HttpResponse = client.post("https://api.openai.com/v1/audio/transcriptions") {
        header("Authorization", "Bearer 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")
        setBody(
            MultiPartFormDataContent(
                formData {
                    append("model", "whisper-1")
                    append("file", file.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "audio/mpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                    })
                }
            )
        )
    }

    println(response.bodyAsText())
    client.close()
}

fun main() {
    val apiKey = System.getenv("apiKey") ?: "3e1888b1c74b493c981866284e11003a"
    val path = "Record (online-voice-recorder.com).mp3"
    println(apiKey)
    GlobalScope.launch {
        try {
//            testGptCheck();
            val uploadedFileUrl = uploadFile(path, apiKey)
            println(getTranscript(uploadedFileUrl, apiKey))
        } catch (e: Exception) {
            e.printStackTrace()
            println(e.message)
        }
    }
    Thread.sleep(30000)
}
