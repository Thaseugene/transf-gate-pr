package storage.trans.com.model.response

data class TranslateMessageResponse(
    val requestId: String,
    val valueToTranslate: ByteArray,
    val lang: String
)
