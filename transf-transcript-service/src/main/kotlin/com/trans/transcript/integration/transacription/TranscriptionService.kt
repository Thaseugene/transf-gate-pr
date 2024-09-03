package com.trans.transcript.integration.transacription

import com.fasterxml.jackson.databind.DeserializationFeature
import com.trans.transcript.dto.PollingResponse
import com.trans.transcript.dto.TranscriptRequest
import com.trans.transcript.dto.TranscriptResponse
import com.trans.transcript.dto.UploadResponse
import com.trans.transcript.exception.TranscriptionExternalException
import com.trans.transcript.integration.client.HttpClientService
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
import io.ktor.util.reflect.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TranscriptionService(
    private val clientService: HttpClientService
) {

    private val messageProvider by inject<MessagingProvider>(MessagingProvider::class.java)

    private val logger: Logger = LoggerFactory.getLogger(TranscriptionService::class.java)

    private val apiKey: String = System.getenv("apiKey");

    private val authHeaders = mapOf("Authorization" to apiKey)

    suspend fun tryToMakeTranscript(bytesToAnalyze: ByteArray): String {
        val uploadedFileUrl = uploadFile(bytesToAnalyze)
        return getTranscript(uploadedFileUrl)
    }

    private suspend fun uploadFile(bytesToAnalyze: ByteArray): String {
        val transcriptResponse = clientService.callHttpService<UploadResponse>(
            UPLOAD_SERVICE_URL,
            HttpMethod.Post,
            authHeaders,
            ContentType.Application.OctetStream,
            bytesToAnalyze
        )
        return transcriptResponse.uploadUrl
    }

    private suspend fun getTranscript(audioUrl: String): String {
        val transcriptResponse = clientService.callHttpService<TranscriptResponse>(
            TRANSCRIPT_URL,
            HttpMethod.Post,
            authHeaders,
            ContentType.Application.Json,
            TranscriptRequest(audioUrl, true)
        )
        val pollingEndpoint = RESULT_URL.format(transcriptResponse.id)
        return checkResult(pollingEndpoint).text
            ?: throw TranscriptionExternalException("Transcription result wasn't reached")
    }

    private suspend fun checkResult(pollingEndpoint: String): PollingResponse {
        val pollingResponse = clientService.callHttpService<PollingResponse>(
            pollingEndpoint,
            HttpMethod.Get,
            authHeaders,
            ContentType.Application.Json
        )
        when (TranscriptionTaskStatus.valueOf(pollingResponse.status.uppercase())) {
            TranscriptionTaskStatus.ERROR -> throw TranscriptionExternalException("Task was ended with error")
            TranscriptionTaskStatus.QUEUED, TranscriptionTaskStatus.PROCESSING -> {
                delay(3000L)
                return checkResult(pollingEndpoint)
            }

            TranscriptionTaskStatus.COMPLETED -> return pollingResponse
        }
    }

}


