package storage.trans.com.service

import com.transf.kafka.messaging.service.ProducingProvider
import com.transf.kafka.messaging.service.type.SenderType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import storage.trans.com.exception.InnerException
import storage.trans.com.model.*
import storage.trans.com.model.request.CommandStrategy
import storage.trans.com.model.request.TelegramMessageRequest
import storage.trans.com.model.request.TranscriptionMessageRequest
import storage.trans.com.model.request.TranslateMessageRequest
import storage.trans.com.model.response.TelegramMessageResponse
import storage.trans.com.persistance.MessageRepository
import storage.trans.com.persistance.UserRepository
import storage.trans.com.service.mapping.*


interface MessageService {

    fun processIncomingMessage(incomingMessage: TelegramMessageRequest, requestId: String)
    fun processIncomingTranscriptMessage(incomingMessage: TranscriptionMessageRequest)
    fun processIncomingTranslateMessage(incomingMessage: TranslateMessageRequest)
    fun updateEvent(event: MessageModel): MessageModel
    fun findMessageById(id: Long): MessageModel

}

class MessageServiceImpl(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
    private val producingProvider: ProducingProvider
) : MessageService {

    private val logger: Logger = LoggerFactory.getLogger(MessageService::class.java)

    override fun processIncomingMessage(incomingMessage: TelegramMessageRequest, requestId: String) {
        when (incomingMessage.executionStrategy) {
            CommandStrategy.TRANSCRIPTION -> processMessageForTranscript(incomingMessage)
            CommandStrategy.TRANSLATION -> processMessageForTranslate(incomingMessage, requestId)
        }
    }

    override fun processIncomingTranscriptMessage(incomingMessage: TranscriptionMessageRequest) {
        runCatching {
            messageRepository.findByRequestId(incomingMessage.requestId)?.let {
                messageRepository.update(
                    it.updateTranscriptFields(incomingMessage)
                )
                producingProvider.prepareMessageToSend(
                    incomingMessage.requestId,
                    it.toTelegramResponse(incomingMessage.messageResult),
                    SenderType.TELEGRAM_SENDER
                )
            }
        }.onFailure {
            logger.error(
                "Exception occurred while while processing message with requestId - " +
                        "${incomingMessage.requestId} from transcription service", it
            )
            producingProvider.prepareMessageToSend(
                incomingMessage.requestId,
                TelegramMessageResponse(status = MessageStatus.ERROR),
                SenderType.TELEGRAM_SENDER
            )
        }
    }

    override fun processIncomingTranslateMessage(incomingMessage: TranslateMessageRequest) {
        runCatching {
                val withNewTranslation = messageRepository.saveTranslation(incomingMessage.toTranslateModel(), incomingMessage.requestId)
                producingProvider.prepareMessageToSend(
                    incomingMessage.requestId,
                    withNewTranslation.toTelegramTranslateResponse(
                        incomingMessage.translatedValue.decode(),
                        incomingMessage.lang
                    ),
                    SenderType.TELEGRAM_SENDER
                )

        }.onFailure {
            logger.error(
                "Exception occurred while while processing message with requestId - " +
                        "${incomingMessage.requestId} from translation service", it
            )
            producingProvider.prepareMessageToSend(
                incomingMessage.requestId,
                TelegramMessageResponse(status = MessageStatus.ERROR),
                SenderType.TELEGRAM_SENDER
            )
        }
    }


    override fun updateEvent(event: MessageModel): MessageModel {
        logger.info("Start updating process of event model - $event")
        return messageRepository.update(event)
    }

    override fun findMessageById(id: Long): MessageModel {
        return messageRepository.findById(id)
    }

    private fun processMessageForTranscript(incomingMessage: TelegramMessageRequest) {
        runCatching {
            if (userRepository.checkIsUserPresented(incomingMessage.userId)) {
                userRepository.save(incomingMessage.toUserModel())
            }
            val savedMessage = messageRepository.save(incomingMessage.toMessageModel())
            producingProvider.prepareMessageToSend(
                savedMessage.requestId,
                savedMessage.toTranscriptResponse(),
                SenderType.TRANSCRIPT_SENDER
            )
        }.onFailure {
            logger.error(
                "Error occurred while processing incoming message with requestId - " +
                        incomingMessage.requestId, it
            )
            producingProvider.prepareMessageToSend(
                incomingMessage.requestId,
                TelegramMessageResponse(status = MessageStatus.ERROR),
                SenderType.TELEGRAM_SENDER
            )
        }
    }

    private fun processMessageForTranslate(incomingMessage: TelegramMessageRequest, requestId: String) {
        runCatching {
            messageRepository.findByRequestId(requestId)?.let { message ->
                if (message.translations != null &&
                    message.translations.any
                    { translateModel -> translateModel.lang?.equals(incomingMessage.lang) == true }
                ) {
                    producingProvider.prepareMessageToSend(
                        incomingMessage.requestId,
                        message.toTelegramTranslateResponse("Translated result has been sent to you before"),
                        SenderType.TELEGRAM_SENDER
                    )
                    return
                }
                message.messageResult?.let { result ->
                    producingProvider.prepareMessageToSend(
                        incomingMessage.requestId,
                        incomingMessage.toTranslateMessageResponse(result),
                        SenderType.TRANSLATE_SENDER
                    )
                }
            }
        }.onFailure {
            producingProvider.prepareMessageToSend(
                incomingMessage.requestId,
                TelegramMessageResponse(status = MessageStatus.ERROR),
                SenderType.TELEGRAM_SENDER
            )
        }
    }
}

