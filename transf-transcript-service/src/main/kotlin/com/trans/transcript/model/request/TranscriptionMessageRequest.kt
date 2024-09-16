package com.trans.transcript.model.request

import com.trans.transcript.model.MessageStatus

data class TranscriptionMessageRequest(
    val requestId: String,
    val messageResult: String,
    val status: MessageStatus
)
