package storage.trans.com.domain

import storage.trans.com.persistance.entity.MessageStatus

data class TranscriptMessageRequest(
    val requestId: String,
    val messageResult: String,
    val status: MessageStatus
)
