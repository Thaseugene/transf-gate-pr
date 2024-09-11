package com.transf.kafka.messaging.common.model.request

import com.transf.kafka.messaging.common.model.MessageStatus

data class TranslateMessageRequest(
    val requestId: String,
    val translatedValue: ByteArray? = null,
    val lang: String? = null,
    val status: MessageStatus
)
