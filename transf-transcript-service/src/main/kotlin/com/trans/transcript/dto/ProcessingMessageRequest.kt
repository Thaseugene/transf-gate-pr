package com.trans.transcript.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProcessingMessageRequest(
    val requestId: String,
    val downloadUrl: ByteArray,
)
