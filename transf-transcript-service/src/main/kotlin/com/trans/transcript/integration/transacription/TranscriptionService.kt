package com.trans.transcript.integration.transacription

import com.fasterxml.jackson.databind.DeserializationFeature
import com.trans.transcript.dto.PollingResponse
import com.trans.transcript.dto.TranscriptRequest
import com.trans.transcript.dto.TranscriptResponse
import com.trans.transcript.dto.UploadResponse
import com.trans.transcript.messaging.MessagingProvider
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TranscriptionService {

    private val messageProvider by inject<MessagingProvider>(MessagingProvider::class.java)

    private val logger: Logger = LoggerFactory.getLogger(TranscriptionService::class.java)

    private val apiKey: String = System.getenv("apiKey");

    companion object {
        val client = HttpClient(OkHttp) {
            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
               jackson {
                   disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
               }
            }
//            engine {
//                proxy = ProxyBuilder.socks("148.113.162.23", 63309)
//            }
            install(HttpTimeout) {
                requestTimeoutMillis = 60000
                socketTimeoutMillis = 60000
                connectTimeoutMillis = 60000
            }
        }
//        val objectMapper = with(jacksonObjectMapper()) {
//            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
//        }
    }

    suspend fun tryToMakeTranscript(bytesToAnalyze: ByteArray): String? {
        try {
            val uploadedFileUrl = uploadFile(bytesToAnalyze)
            return getTranscript(uploadedFileUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            println(e.message)
        }
        return null;
    }

    private suspend fun uploadFile(bytesToAnalyze: ByteArray? = null): String = withContext(IO) {
        val transcriptResponse: UploadResponse = client.post(UPLOAD_SERVICE_URL) {
            contentType(ContentType.Application.OctetStream)
            header("Authorization", apiKey)
            setBody(bytesToAnalyze)
        }.body()
//        val transcriptResponse = objectMapper.readValue(
//            response.bodyAsText(),
//            object : TypeReference<UploadResponse>() {})
        transcriptResponse.uploadUrl
    }

    private suspend fun getTranscript(audioUrl: String) = withContext(IO) {
        val transcriptResponse: TranscriptResponse = client.post(TRANSCRIPT_URL) {
            contentType(ContentType.Application.Json)
            header("Authorization", apiKey)
            setBody(TranscriptRequest(audioUrl, true))
        }.body()
//        val transcriptResponse = objectMapper.readValue(
//            response.bodyAsText(),
//            object : TypeReference<TranscriptResponse>() {})
        val pollingEndpoint = RESULT_URL.format(transcriptResponse.id)
        checkResult(pollingEndpoint, apiKey).text
    }

    private suspend fun checkResult(pollingEndpoint: String, apiKey: String): PollingResponse {
        val pollingResponse: PollingResponse = client.get(pollingEndpoint) {
            header("Authorization", apiKey)
        }.body()
//        val pollingResponse = objectMapper.readValue(
//            response.bodyAsText(),
//            object : TypeReference<PollingResponse>() {})
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

}


