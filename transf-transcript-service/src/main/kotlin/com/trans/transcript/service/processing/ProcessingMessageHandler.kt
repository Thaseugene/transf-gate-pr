package com.trans.transcript.service.processing

import com.trans.transcript.dto.MessageStatus
import com.trans.transcript.dto.ProcessingMessageRequest
import com.trans.transcript.integration.transacription.TranscriptionService
import com.trans.transcript.service.mapping.prepareProcessingResponse
import com.transf.kafka.messaging.HandlerType
import com.transf.kafka.messaging.MessagingProvider
import com.transf.kafka.messaging.SenderType
import com.transf.kafka.messaging.service.MessageHandler
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class ProcessingMessageHandler(
    private val dispatcher: CoroutineDispatcher,
    private val transcriptionService: TranscriptionService,
    private val messagingProvider: MessagingProvider
) : MessageHandler<ProcessingMessageRequest> {

    private val logger: Logger = LoggerFactory.getLogger(ProcessingMessageHandler::class.java)

    private val empty = "ERROR"

    companion object {
        val client = HttpClient(OkHttp) {
            install(Logging) {
                level = LogLevel.ALL
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                socketTimeoutMillis = 30000
                connectTimeoutMillis = 30000
            }
        }
    }

    override fun handleMessage(message: ConsumerRecord<String, ProcessingMessageRequest>) {
        CoroutineScope(dispatcher).launch {
            message.value()?.let {
                logger.info("Message -> ${message.value()}")
                try {
                    val downloadUrl = Base64.getDecoder().decode(message.value().downloadUrl).decodeToString()
                    val response: HttpResponse = client.get(downloadUrl)
                    val result = transcriptionService.tryToMakeTranscript(response.readBytes())
                    messagingProvider.prepareMessageToSend(
                        message.value().requestId,
                        prepareProcessingResponse(
                            message.value().requestId,
                            if (result.isNullOrEmpty()) MessageStatus.ERROR else MessageStatus.OK,
                            result ?: empty),
                        SenderType.PROCESSING_SENDER
                    )
                } catch (ex: Exception) {
                    sendErrorMessage(message.value().requestId)
                }

            }
        }
    }

    override fun getType(): HandlerType {
        return HandlerType.PROCESSING_HANDLER
    }

    private fun sendErrorMessage(requestId: String) {
        messagingProvider.prepareMessageToSend(
            requestId,
            prepareProcessingResponse(requestId, MessageStatus.ERROR, empty),
            SenderType.PROCESSING_SENDER
        )
    }

}
