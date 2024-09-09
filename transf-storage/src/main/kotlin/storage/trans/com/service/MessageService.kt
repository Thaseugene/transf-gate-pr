package storage.trans.com.service

import storage.trans.com.exception.InnerException
import com.trans.exception.RepositoryException
import com.trans.service.mapping.*
import com.transf.kafka.messaging.service.ProducingProvider
import com.transf.kafka.messaging.service.type.SenderType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import storage.trans.com.domain.*
import storage.trans.com.persistance.MessageRepository
import storage.trans.com.persistance.UserRepository
import storage.trans.com.persistance.entity.MessageStatus


interface MessageService {

    fun processIncomingMessage(incomingMessage: TelegramMessageRequest)
    fun processIncomingTranscriptMessage(incomingMessage: TranscriptMessageRequest)
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

    override fun processIncomingMessage(incomingMessage: TelegramMessageRequest) {
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
        } catch (ex: RepositoryException) {
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

    override fun processIncomingTranscriptMessage(incomingMessage: TranscriptMessageRequest) {
        try {
            val updatedMessage = messageRepository.update(
                messageRepository.findByRequestId(incomingMessage.requestId)
                    .updateTranscriptFields(incomingMessage)
            )
            val result = incomingMessage.messageResult
            producingProvider.prepareMessageToSend(
                incomingMessage.requestId,
                updatedMessage.toTelegramResponse(result),
                SenderType.TELEGRAM_SENDER
            )
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
            val updatedMessage = messageRepository.update(
                messageRepository.findByRequestId(incomingMessage.requestId)
                    .updateTranslateFields(incomingMessage)
            )
            val result = incomingMessage.translatedValue
            producingProvider.prepareMessageToSend(
                incomingMessage.requestId,
                updatedMessage.toTelegramTranslateResponse(
                    result ?: throw InnerException("Value result wasn't found in response")
                ),
                SenderType.TELEGRAM_SENDER
            )
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

}

