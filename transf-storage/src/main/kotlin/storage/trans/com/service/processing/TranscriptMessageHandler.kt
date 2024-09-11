package storage.trans.com.service.processing

import com.transf.kafka.messaging.service.MessageHandler
import com.transf.kafka.messaging.service.type.HandlerType
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import storage.trans.com.model.request.TranscriptMessageRequest
import storage.trans.com.service.MessageService

class TranscriptMessageHandler(
    private val messageService: MessageService
) : MessageHandler<TranscriptMessageRequest> {

    private val logger: Logger = LoggerFactory.getLogger(TranscriptMessageHandler::class.java)

    override fun handleMessage(message: ConsumerRecord<String, TranscriptMessageRequest>) {
        logger.info("Message -> ${message.value()}")
        messageService.processIncomingTranscriptMessage(message.value())
    }

    override fun getType() = HandlerType.TRANSCRIPT_HANDLER

}
