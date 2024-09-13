package storage.trans.com.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Serializable
data class MessageModel(
    val id: Long,
    val userId: Long,
    val requestId: String,
    val chatId: Long,
    val messageId: Long,
    var timeStamp: Long,
    val messageValue: ByteArray,
    var messageResult: ByteArray? = null,
    var translateResult: ByteArray? = null,
    var lang: String? = null,
    var status: MessageStatus? = null,
) {

    val timeStampDate: LocalDateTime
        get() {
            val instant = Instant.ofEpochMilli(timeStamp)
            return LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.id))
        }

}
