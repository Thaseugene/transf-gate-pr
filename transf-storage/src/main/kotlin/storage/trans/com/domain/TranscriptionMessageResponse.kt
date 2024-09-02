package storage.trans.com.domain

data class TranscriptionMessageResponse(
    val requestId: String,
    val downloadUrl: ByteArray,
)
