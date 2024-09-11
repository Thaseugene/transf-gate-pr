package com.trans.telegram.model.response

import com.trans.telegram.model.MessageStatus
import kotlinx.serialization.Serializable

@Serializable
data class ProcessingMessageResponse(
    val requestId: String?,
    val chatId: Long?,
    val messageId: Long?,
    val result: String?,
    val translatedResult: String?,
    val status: MessageStatus?
)
