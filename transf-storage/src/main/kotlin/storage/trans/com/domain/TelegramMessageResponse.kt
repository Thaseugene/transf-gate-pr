package storage.trans.com.domain

import kotlinx.serialization.Serializable
import storage.trans.com.persistance.entity.MessageStatus

@Serializable
data class TelegramMessageResponse(
    val requestId: String? = null,
    val chatId: Long? = null,
    val messageId: Long? = null,
    val result: String? = null,
    val status: MessageStatus
)
