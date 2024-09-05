package storage.trans.com.service.processing

import com.transf.kafka.messaging.service.MessageHandler
import com.transf.kafka.messaging.service.type.HandlerType
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import storage.trans.com.domain.TelegramMessageRequest
import storage.trans.com.service.MessageService

class TelegramMessageHandler(
    private val messageService: MessageService
) : MessageHandler<TelegramMessageRequest> {

    private val logger: Logger = LoggerFactory.getLogger(TelegramMessageHandler::class.java)

    override fun handleMessage(message: ConsumerRecord<String, TelegramMessageRequest>) {
        logger.info("Message -> ${message.value()}")
        messageService.processIncomingMessage(message.value())
    }

    override fun getType(): HandlerType {
        return HandlerType.TELEGRAM_HANDLER
    }

}
