package com.trans.translate.service

import com.transf.kafka.messaging.service.ProducingProvider
import com.transf.kafka.messaging.service.type.SenderType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.trans.translate.model.MessageStatus
import com.trans.translate.model.request.TranslateMessageRequest
import com.trans.translate.model.response.TranslateMessageResponse
import com.trans.translate.service.integration.translate.TranslateService
import com.trans.translate.service.mapping.prepareTextInput
import com.trans.translate.service.mapping.toProcessingResponse
import com.trans.translate.service.mapping.toTranslateMessage

interface MessageService {

    suspend fun processTranslateMessage(message: TranslateMessageRequest)

    suspend fun sendErrorMessage(requestId: String)

}

class MessageServiceImpl(
    private val producingProvider: ProducingProvider,
    private val translateService: TranslateService
) : MessageService {

    private val logger: Logger = LoggerFactory.getLogger(MessageServiceImpl::class.java)

    override suspend fun processTranslateMessage(message: TranslateMessageRequest) {
        runCatching {
            val result = prepareTextInput(message.valueToTranslate)
                .map { translateService.prepareTranslation(message.toTranslateMessage(it)) }
                .reduce { acc, s -> acc.plus(s) }
            producingProvider.prepareMessageToSend(
                message.requestId,
                result.toProcessingResponse(message.requestId, message.lang),
                SenderType.PROCESSING_SENDER
            )
        }.onFailure {
            logger.error("Unexpected error while processing message for translate", it)
            sendErrorMessage(message.requestId)
        }
    }

    override suspend fun sendErrorMessage(requestId: String) {
        producingProvider.prepareMessageToSend(
            requestId,
            TranslateMessageResponse(requestId, status = MessageStatus.ERROR),
            SenderType.PROCESSING_SENDER
        )
    }

}
