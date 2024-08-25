package com.trans.dto

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable

@Serializable
data class UploadResponse(
    @JsonProperty("upload_url") val uploadUrl: String
)
