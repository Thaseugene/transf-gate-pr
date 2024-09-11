package com.trans.translate.model.response

import com.trans.translate.model.MessageStatus

data class ProcessingMessageResponse(
    val requestId: String,
    val translatedValue: ByteArray? = null,
    val lang: String? = null,
    val status: MessageStatus,
)
