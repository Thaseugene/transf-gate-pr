package com.trans.translate.model.request

import kotlinx.serialization.Serializable

@Serializable
data class TranslateMessageRequest(
    val requestId: String,
    val valueToTranslate: ByteArray,
    val lang: String,
)
