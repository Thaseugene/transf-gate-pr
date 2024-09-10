package com.trans.translate.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProcessingMessageRequest(
    val requestId: String,
    val valueToTranslate: String,
    val lang: String
)
