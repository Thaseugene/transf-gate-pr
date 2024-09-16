package com.trans.translate.model.response

import kotlinx.serialization.Serializable

@Serializable
data class TranslateValueResponse(
    val to: String,
    val translated: List<String>,
)
