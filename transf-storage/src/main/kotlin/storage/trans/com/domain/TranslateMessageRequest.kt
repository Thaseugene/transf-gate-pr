package storage.trans.com.domain

import storage.trans.com.persistance.entity.MessageStatus

data class TranslateMessageRequest(
    val requestId: String,
    val translatedValue: String? = null,
    val status: MessageStatus
)
