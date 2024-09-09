package translate.transf.com.dto

data class ProcessingMessageResponse(
    val requestId: String,
    val translatedValue: String? = null,
    val status: MessageStatus
)
