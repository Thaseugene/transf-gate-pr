package com.trans.transcript.integration.transacription

import com.trans.transcript.model.response.PollingResponse
import com.trans.transcript.model.request.TranscriptRequest
import com.trans.transcript.model.response.TranscriptResponse
import com.trans.transcript.model.response.UploadResponse
import com.trans.transcript.exception.TranscriptionExternalException
import com.trans.transcript.integration.client.HttpClientService
import io.ktor.http.*
import kotlinx.coroutines.delay
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface TranscriptionService {

    suspend fun tryToMakeTranscript(bytesToAnalyze: ByteArray): String

}

class TranscriptionServiceImpl(
    private val clientService: HttpClientService
): TranscriptionService {

    private val logger: Logger = LoggerFactory.getLogger(TranscriptionServiceImpl::class.java)

    private val apiKey: String = System.getenv("apiKey");

    private val authHeaders = mapOf("Authorization" to apiKey)

    override suspend fun tryToMakeTranscript(bytesToAnalyze: ByteArray): String {
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


