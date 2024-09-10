package com.trans.translate.dto

data class ProcessingMessageResponse(
    val requestId: String,
    val translatedValue: ByteArray? = null,
    val lang: String? = null,
    val status: MessageStatus
)
