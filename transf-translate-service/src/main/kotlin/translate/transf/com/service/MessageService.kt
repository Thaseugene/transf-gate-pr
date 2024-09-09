package translate.transf.com.service

import com.transf.kafka.messaging.service.ProducingProvider
import com.transf.kafka.messaging.service.type.SenderType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import translate.transf.com.dto.MessageStatus
import translate.transf.com.dto.ProcessingMessageRequest
import translate.transf.com.dto.ProcessingMessageResponse
import translate.transf.com.integration.client.HttpClientService
import translate.transf.com.integration.translate.TranslateService
import translate.transf.com.integration.translate.TranslateServiceImpl
import translate.transf.com.service.mapping.toProcessingResponse
import translate.transf.com.service.mapping.toTranslateMessage

interface MessageService {

    suspend fun processTranslateMessage(message: ProcessingMessageRequest)

    suspend fun sendErrorMessage(requestId: String)

}

class MessageServiceImpl(
    private val producingProvider: ProducingProvider,
    private val translateService: TranslateService
) : MessageService {

    private val logger: Logger = LoggerFactory.getLogger(MessageServiceImpl::class.java)

    override suspend fun processTranslateMessage(message: ProcessingMessageRequest) {
        try {
            val translatedResult = translateService.prepareTranslation(message.toTranslateMessage())
            producingProvider.prepareMessageToSend(
                message.requestId,
                translatedResult.toProcessingResponse(message.requestId),
                SenderType.PROCESSING_SENDER
            )
        } catch (ex: Exception) {
            logger.error("Unexpected error while processing message for translate", ex)
            sendErrorMessage(message.requestId)
        }
    }

    override suspend fun sendErrorMessage(requestId: String) {
        producingProvider.prepareMessageToSend(
            requestId,
            ProcessingMessageResponse(requestId, status = MessageStatus.ERROR),
            SenderType.PROCESSING_SENDER
        )
    }

}
