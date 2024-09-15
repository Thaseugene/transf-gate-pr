package com.trans.transcript.service

import com.trans.transcript.model.MessageStatus
import com.trans.transcript.service.integration.transacription.TranscriptionService
import com.trans.transcript.service.mapping.prepareProcessingResponse
import com.transf.kafka.messaging.service.ProducingProvider
import com.transf.kafka.messaging.service.type.SenderType
import io.ktor.client.statement.*

interface MessageService {

    suspend fun processTranscriptionMessage(response: HttpResponse, requestId: String)

    suspend fun sendErrorMessage(requestId: String)

}

class MessageServiceImpl(
    private val transcriptionService: TranscriptionService,
    private val producingProvider: ProducingProvider
): MessageService {

    override suspend fun processTranscriptionMessage(response: HttpResponse, requestId: String) {
        val result = transcriptionService.tryToMakeTranscript(response.readBytes())
        producingProvider.prepareMessageToSend(
            requestId,
            prepareProcessingResponse(
                requestId,
                if (result.isEmpty()) MessageStatus.ERROR else MessageStatus.OK,
                result
            ),
            SenderType.PROCESSING_SENDER
        )
    }

    override suspend fun sendErrorMessage(requestId: String) {
        producingProvider.prepareMessageToSend(
            requestId,
            prepareProcessingResponse(requestId, MessageStatus.ERROR, MessageStatus.ERROR.name),
            SenderType.PROCESSING_SENDER
        )
    }
}
