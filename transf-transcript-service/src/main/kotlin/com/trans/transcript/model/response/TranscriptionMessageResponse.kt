package com.trans.transcript.model.response

data class TranscriptionMessageResponse(
    val requestId: String,
    val downloadUrl: ByteArray,
)
