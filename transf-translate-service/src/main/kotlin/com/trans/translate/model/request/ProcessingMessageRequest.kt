package com.trans.translate.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ProcessingMessageRequest(
    val requestId: String,
    val valueToTranslate: ByteArray,
    val lang: String,
)
