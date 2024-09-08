package translate.transf.com.dto

import kotlinx.serialization.Serializable

@Serializable
data class TranslateValueResponse(
    val to: String,
    val translated: List<String>
)
