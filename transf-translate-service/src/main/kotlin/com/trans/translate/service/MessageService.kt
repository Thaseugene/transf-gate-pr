package com.trans.translate.service

import com.transf.kafka.messaging.service.ProducingProvider
import com.transf.kafka.messaging.service.type.SenderType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.trans.translate.model.MessageStatus
import com.trans.translate.model.request.ProcessingMessageRequest
import com.trans.translate.model.response.ProcessingMessageResponse
import com.trans.translate.integration.translate.TranslateService
import com.trans.translate.service.mapping.toProcessingResponse
import com.trans.translate.service.mapping.toTranslateMessage

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
                translatedResult.toProcessingResponse(message.requestId, message.lang),
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
