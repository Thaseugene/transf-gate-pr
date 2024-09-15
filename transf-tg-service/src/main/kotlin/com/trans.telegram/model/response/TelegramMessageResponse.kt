package com.trans.telegram.model.response

import com.trans.telegram.model.MessageStatus
import kotlinx.serialization.Serializable

@Serializable
data class TelegramMessageResponse(
    val requestId: String? = null,
    val chatId: Long? = null,
    val messageId: Long? = null,
    val result: String? = null,
    val lang: String? = null,
    val translatedResult: String? = null,
    val status: MessageStatus,
)
