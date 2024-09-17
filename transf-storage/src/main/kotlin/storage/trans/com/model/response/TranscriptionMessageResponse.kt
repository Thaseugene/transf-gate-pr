package storage.trans.com.model.response

data class TranscriptionMessageResponse(
    val requestId: String,
    val downloadUrl: ByteArray,
)
