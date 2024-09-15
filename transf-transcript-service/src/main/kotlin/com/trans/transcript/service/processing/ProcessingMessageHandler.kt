package com.trans.transcript.service.processing

import com.trans.transcript.model.response.TranscriptionMessageResponse
import com.trans.transcript.service.integration.client.HttpClientService
import com.trans.transcript.service.MessageService
import com.transf.kafka.messaging.service.type.HandlerType
import com.transf.kafka.messaging.service.MessageHandler
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class ProcessingMessageHandler(
    private val dispatcher: CoroutineDispatcher,
    private val messageService: MessageService,
    private val clientService: HttpClientService
) : MessageHandler<TranscriptionMessageResponse> {

    private val logger: Logger = LoggerFactory.getLogger(ProcessingMessageHandler::class.java)

    override fun handleMessage(message: ConsumerRecord<String, TranscriptionMessageResponse>) {
        CoroutineScope(dispatcher).launch {
            message.value()?.let {
                logger.info("Received Message -> ${message.value()}")
                try {
                    val downloadUrl = Base64.getDecoder().decode(message.value().downloadUrl).decodeToString()
                    val response: HttpResponse = clientService.callHttpService(downloadUrl, HttpMethod.Get)
                    messageService.processTranscriptionMessage(response, message.value().requestId)
                } catch (ex: Exception) {
                    messageService.sendErrorMessage(message.value().requestId)
                }

            }
        }
    }

    override fun getType(): HandlerType = HandlerType.PROCESSING_HANDLER

    override fun getGenericType(): Class<TranscriptionMessageResponse> = TranscriptionMessageResponse::class.java

}
