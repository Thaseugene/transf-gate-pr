package com.transf.kafka.messaging.common.model.response

import com.transf.kafka.messaging.common.model.MessageStatus
import kotlinx.serialization.Serializable

@Serializable
data class TelegramMessageResponse(
    val requestId: String? = null,
    val chatId: Long? = null,
    val messageId: Long? = null,
    val result: String? = null,
    val translatedResult: String? = null,
    val status: MessageStatus
)
