package translate.transf.com.service

import com.transf.kafka.messaging.service.ProducingProvider
import translate.transf.com.dto.ProcessingMessageRequest
import translate.transf.com.integration.client.HttpClientService

interface MessageService {

    suspend fun processTranslateMessage(message: ProcessingMessageRequest)

    suspend fun sendErrorMessage(requestId: String)

}

class MessageServiceImpl(
    val clientService: HttpClientService,
    private val producingProvider: ProducingProvider
): MessageService {

    override suspend fun processTranslateMessage(message: ProcessingMessageRequest) {
        TODO("Not yet implemented")
    }

    override suspend fun sendErrorMessage(requestId: String) {
        TODO("Not yet implemented")
    }

}
