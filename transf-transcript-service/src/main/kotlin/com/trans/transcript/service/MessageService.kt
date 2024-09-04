package com.trans.transcript.service

import com.trans.transcript.dto.MessageStatus
import com.trans.transcript.integration.transacription.TranscriptionService
import com.trans.transcript.service.mapping.prepareProcessingResponse
import com.transf.kafka.messaging.MessagingProvider
import com.transf.kafka.messaging.SenderType
import io.ktor.client.statement.*
import org.koin.java.KoinJavaComponent.inject

interface MessageService {

    suspend fun processTranscriptionMessage(response: HttpResponse, requestId: String)

    suspend fun sendErrorMessage(requestId: String)

}

class MessageServiceImpl: MessageService {

    private val transcriptionService: TranscriptionService by inject(TranscriptionService::class.java)

    private val messagingProvider: MessagingProvider  by inject(MessagingProvider::class.java)

    override suspend fun processTranscriptionMessage(response: HttpResponse, requestId: String) {
        val result = transcriptionService.tryToMakeTranscript(response.readBytes())
        messagingProvider.prepareMessageToSend(
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
        messagingProvider.prepareMessageToSend(
            requestId,
            prepareProcessingResponse(requestId, MessageStatus.ERROR, MessageStatus.ERROR.name),
            SenderType.PROCESSING_SENDER
        )
    }
}
