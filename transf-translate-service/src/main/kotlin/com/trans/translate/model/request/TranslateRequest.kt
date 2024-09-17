package com.trans.translate.model.request

import kotlinx.serialization.Serializable

@Serializable
data class TranslateRequest(
    val texts: List<String>,
    val to: List<String>,
)
