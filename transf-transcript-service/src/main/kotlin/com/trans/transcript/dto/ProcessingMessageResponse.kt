package com.trans.transcript.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProcessingMessageResponse(
    val requestId: String,
    val messageResult: String,
    val status: MessageStatus
)
