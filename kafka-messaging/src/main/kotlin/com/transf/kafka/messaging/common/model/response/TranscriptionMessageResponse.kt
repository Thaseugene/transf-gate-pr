package com.transf.kafka.messaging.common.model.response

data class TranscriptionMessageResponse(
    val requestId: String,
    val downloadUrl: ByteArray,
)
