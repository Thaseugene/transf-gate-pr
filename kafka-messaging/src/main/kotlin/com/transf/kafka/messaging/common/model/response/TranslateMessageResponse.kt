package com.transf.kafka.messaging.common.model.response

data class TranslateMessageResponse(
    val requestId: String,
    val valueToTranslate: ByteArray,
    val lang: String
)
