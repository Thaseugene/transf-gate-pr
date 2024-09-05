package storage.trans.com.service

import com.trans.exception.RepositoryException
import com.trans.service.mapping.*
import com.transf.kafka.messaging.service.ProducingProvider
import com.transf.kafka.messaging.service.type.SenderType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import storage.trans.com.domain.MessageModel
import storage.trans.com.domain.TelegramMessageRequest
import storage.trans.com.domain.TranscriptMessageRequest
import storage.trans.com.persistance.MessageRepository
import storage.trans.com.persistance.UserRepository


interface MessageService {

    fun processIncomingMessage(incomingMessage: TelegramMessageRequest)
    fun processIncomingTranscriptMessage(incomingMessage: TranscriptMessageRequest)
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
                SenderType.TRANSCRIPT_SENDER)
        } catch (ex: RepositoryException) {
            logger.error("Error occurred while processing incoming message", ex)
        }
    }

    override fun processIncomingTranscriptMessage(incomingMessage: TranscriptMessageRequest) {
        try {
            val updatedMessage = messageRepository.update(
                messageRepository.findByRequestId(incomingMessage.requestId)
                    .updateTranscriptFields(incomingMessage))
            val result = incomingMessage.messageResult
            producingProvider.prepareMessageToSend(
                incomingMessage.requestId,
                updatedMessage.toTelegramResponse(result),
                SenderType.TELEGRAM_SENDER)
        } catch (ex: Exception) {
            logger.error("Exception occurred while while processing message with requestId - " +
                    "${incomingMessage.requestId} from transcription service", ex)
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

