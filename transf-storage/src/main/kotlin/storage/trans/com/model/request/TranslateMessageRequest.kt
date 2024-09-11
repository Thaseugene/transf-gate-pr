package storage.trans.com.model.request

import storage.trans.com.persistance.entity.MessageStatus

data class TranslateMessageRequest(
    val requestId: String,
    val translatedValue: ByteArray? = null,
    val lang: String? = null,
    val status: MessageStatus
)
