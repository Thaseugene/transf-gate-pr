package storage.trans.com.model.response

import kotlinx.serialization.Serializable
import storage.trans.com.model.MessageStatus

@Serializable
data class TelegramMessageResponse(
    val requestId: String? = null,
    val chatId: Long? = null,
    val messageId: Long? = null,
    val result: String? = null,
    val translatedResult: String? = null,
    val status: MessageStatus,
)
