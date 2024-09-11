package com.trans.transcript.model.response

import com.trans.transcript.model.MessageStatus
import kotlinx.serialization.Serializable

@Serializable
data class ProcessingMessageResponse(
    val requestId: String,
    val messageResult: String,
    val status: MessageStatus,
)
