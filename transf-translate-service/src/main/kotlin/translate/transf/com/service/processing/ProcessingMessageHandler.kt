package translate.transf.com.service.processing

import com.transf.kafka.messaging.service.MessageHandler
import com.transf.kafka.messaging.service.type.HandlerType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import translate.transf.com.dto.ProcessingMessageRequest
import translate.transf.com.service.MessageService

class ProcessingMessageHandler (
    private val dispatcher: CoroutineDispatcher,
    private val messageService: MessageService,
): MessageHandler<ProcessingMessageRequest> {

    private val logger: Logger = LoggerFactory.getLogger(ProcessingMessageHandler::class.java)

    override fun handleMessage(message: ConsumerRecord<String, ProcessingMessageRequest>) {
        CoroutineScope(dispatcher).launch {
            message.value()?.let {
                logger.info("Received Message -> ${message.value()}")
                messageService.processTranslateMessage(message.value())
            }
        }
    }

    override fun getType(): HandlerType {
        return HandlerType.PROCESSING_HANDLER
    }

}

