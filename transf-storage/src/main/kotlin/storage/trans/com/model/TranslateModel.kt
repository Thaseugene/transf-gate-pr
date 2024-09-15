package storage.trans.com.model

import kotlinx.serialization.Serializable

@Serializable
data class TranslateModel(
    var translateResult: ByteArray? = null,
    var lang: String? = null,
)
