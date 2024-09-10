package com.trans.translate.dto

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable

@Serializable
data class TranslateResponse(
    val translations: List<TranslateValueResponse>,
    val from: String,
    @JsonProperty("translated_characters")
    val translatedCharacters: Long
)
