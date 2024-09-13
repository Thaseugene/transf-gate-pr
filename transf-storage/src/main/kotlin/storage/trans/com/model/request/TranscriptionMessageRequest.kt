package storage.trans.com.model.request

import storage.trans.com.model.MessageStatus

data class TranscriptionMessageRequest(
    val requestId: String,
    val messageResult: String,
    val status: MessageStatus,
)
