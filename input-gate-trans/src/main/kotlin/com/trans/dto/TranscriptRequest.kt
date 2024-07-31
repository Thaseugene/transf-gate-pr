package com.trans.dto

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable

@Serializable
data class TranscriptRequest(
    @JsonProperty("audio_url") val audioUrl: String
)
