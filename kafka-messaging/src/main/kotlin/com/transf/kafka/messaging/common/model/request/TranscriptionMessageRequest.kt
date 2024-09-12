package com.transf.kafka.messaging.common.model.request

import com.transf.kafka.messaging.common.model.MessageStatus

data class TranscriptionMessageRequest(
    val requestId: String,
    val messageResult: String,
    val status: MessageStatus
)
