package translate.transf.com.service.processing

import com.transf.kafka.messaging.service.MessageHandler
import com.transf.kafka.messaging.service.type.HandlerType
import kotlinx.coroutines.CoroutineDispatcher
import org.apache.kafka.clients.consumer.ConsumerRecord
import translate.transf.com.dto.ProcessingMessageRequest
import translate.transf.com.service.MessageService

class ProcessingMessageHandler (
    private val dispatcher: CoroutineDispatcher,
    private val messageService: MessageService,
): MessageHandler<ProcessingMessageRequest> {

    override fun handleMessage(message: ConsumerRecord<String, ProcessingMessageRequest>) {
        TODO("Not yet implemented")
    }

    override fun getType(): HandlerType {
        TODO("Not yet implemented")
    }

}

