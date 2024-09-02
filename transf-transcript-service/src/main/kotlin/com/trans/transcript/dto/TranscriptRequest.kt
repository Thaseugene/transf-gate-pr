package com.trans.transcript.dto

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable

@Serializable
data class TranscriptRequest(
    @JsonProperty("audio_url") val audioUrl: String,
    @JsonProperty("language_detection") val languageDetection: Boolean
)
