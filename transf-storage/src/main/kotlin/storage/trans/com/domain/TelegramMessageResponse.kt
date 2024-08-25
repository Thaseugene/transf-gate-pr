package storage.trans.com.domain

import kotlinx.serialization.Serializable

@Serializable
data class TelegramMessageResponse(
    val requestId: String,
    val chatId: Long,
    val messageId: Long,
    val result: String
)
