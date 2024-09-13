package storage.trans.com.service

import com.transf.kafka.messaging.service.ProducingProvider
import com.transf.kafka.messaging.service.type.SenderType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
        try {
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
        } catch (ex: Exception) {
            logger.error(
                "Exception occurred while while processing message with requestId - " +
                        "${incomingMessage.requestId} from transcription service", ex
            )
            producingProvider.prepareMessageToSend(
                incomingMessage.requestId,
                TelegramMessageResponse(status = MessageStatus.ERROR),
                SenderType.TELEGRAM_SENDER
            )
        }
    }

    override fun processIncomingTranslateMessage(incomingMessage: TranslateMessageRequest) {
        try {
            val existingMessage = messageRepository.findByRequestId(incomingMessage.requestId)
            existingMessage?.let {
                it.updateTranslateFields(incomingMessage)
                producingProvider.prepareMessageToSend(
                    incomingMessage.requestId,
                    messageRepository.update(it).toTelegramTranslateResponse(
                        incomingMessage.translatedValue.decode()
                    ),
                    SenderType.TELEGRAM_SENDER
                )
            }
        } catch (ex: Exception) {
            logger.error(
                "Exception occurred while while processing message with requestId - " +
                        "${incomingMessage.requestId} from translation service", ex
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
        try {
            if (userRepository.checkIsUserPresented(incomingMessage.userId)) {
                userRepository.save(incomingMessage.toUserModel())
            }
            val savedMessage = messageRepository.save(incomingMessage.toMessageModel())
            producingProvider.prepareMessageToSend(
                savedMessage.requestId,
                savedMessage.toTranscriptResponse(),
                SenderType.TRANSCRIPT_SENDER
            )
        } catch (ex: Exception) {
            logger.error(
                "Error occurred while processing incoming message with requestId - " +
                        incomingMessage.requestId, ex
            )
            producingProvider.prepareMessageToSend(
                incomingMessage.requestId,
                TelegramMessageResponse(status = MessageStatus.ERROR),
                SenderType.TELEGRAM_SENDER
            )
        }
    }

    private fun processMessageForTranslate(incomingMessage: TelegramMessageRequest, requestId: String) {
        try {
            messageRepository.findByRequestId(requestId)?.let { message ->
                if (message.translateResult != null && message.lang?.equals(incomingMessage.lang) == true) {
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
        } catch (ex: Exception) {
            producingProvider.prepareMessageToSend(
                incomingMessage.requestId,
                TelegramMessageResponse(status = MessageStatus.ERROR),
                SenderType.TELEGRAM_SENDER
            )
        }
    }
}

