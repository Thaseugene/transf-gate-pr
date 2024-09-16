package com.trans.transcript.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PollingResponse(
    val status: String,
    val text: String? = null,
    val error: String? = null
)
