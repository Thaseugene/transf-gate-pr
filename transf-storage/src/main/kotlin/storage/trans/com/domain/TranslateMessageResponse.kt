package storage.trans.com.domain

data class TranslateMessageResponse(
    val requestId: String,
    val valueToTranslate: ByteArray,
    val lang: String
)
