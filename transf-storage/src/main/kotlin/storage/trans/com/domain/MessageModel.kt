package storage.trans.com.domain

import kotlinx.serialization.Serializable
import storage.trans.com.persistance.entity.MessageStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

const val DEFAULT_DATE_ZONE = "UTC"

@Serializable
data class MessageModel(
    val id: Long,
    val userId: Long,
    val requestId: String,
    val chatId: Long,
    val messageId: Long,
    val timeStamp: Long,
    val messageValue: ByteArray,
    var messageResult: ByteArray? = null,
    var translateResult: ByteArray? = null,
    var status: MessageStatus? = null
) {

    val timeStampDate: LocalDateTime
        get() {
            val instant = Instant.ofEpochMilli(timeStamp)
            return LocalDateTime.ofInstant(instant, ZoneId.of(DEFAULT_DATE_ZONE))
        }

}
