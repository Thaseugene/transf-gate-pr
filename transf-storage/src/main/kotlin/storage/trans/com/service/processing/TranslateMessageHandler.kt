package storage.trans.com.service.processing

import com.transf.kafka.messaging.service.MessageHandler
import com.transf.kafka.messaging.service.type.HandlerType
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import storage.trans.com.domain.TranslateMessageRequest
import storage.trans.com.service.MessageService

class TranslateMessageHandler(
    private val messageService: MessageService
) : MessageHandler<TranslateMessageRequest> {

    private val logger: Logger = LoggerFactory.getLogger(TranslateMessageHandler::class.java)

    override fun handleMessage(message: ConsumerRecord<String, TranslateMessageRequest>) {
        logger.info("Message -> ${message.value()}")
        messageService.processIncomingTranslateMessage(message.value())
    }

    override fun getType() = HandlerType.TRANSLATE_HANDLER

}
