package com.trans.domain

import kotlinx.serialization.Serializable

@Serializable
data class ProcessingMessageResponse(
    val requestId: String,
    val chatId: Long,
    val messageId: Long,
    val result: String
)
