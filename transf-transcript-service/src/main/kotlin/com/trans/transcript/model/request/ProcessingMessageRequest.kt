package com.trans.transcript.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ProcessingMessageRequest(
    val requestId: String,
    val downloadUrl: ByteArray,
)
