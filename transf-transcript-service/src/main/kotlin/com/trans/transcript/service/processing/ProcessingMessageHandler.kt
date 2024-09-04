package com.trans.transcript.service.processing

import com.trans.transcript.dto.ProcessingMessageRequest
import com.trans.transcript.integration.client.HttpClientService
import com.trans.transcript.service.MessageService
import com.transf.kafka.messaging.HandlerType
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
) : MessageHandler<ProcessingMessageRequest> {

    private val logger: Logger = LoggerFactory.getLogger(ProcessingMessageHandler::class.java)

    override fun handleMessage(message: ConsumerRecord<String, ProcessingMessageRequest>) {
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

    override fun getType(): HandlerType {
        return HandlerType.PROCESSING_HANDLER
    }

}
