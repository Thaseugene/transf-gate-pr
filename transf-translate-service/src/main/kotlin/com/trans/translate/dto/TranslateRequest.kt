package com.trans.translate.dto

import kotlinx.serialization.Serializable

@Serializable
data class TranslateRequest(
    val texts: List<String>,
    val to: List<String>
)
