package com.trans.domain

import kotlinx.serialization.Serializable
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
    val messageValue: ByteArray? = null,
    val messageResult: ByteArray? = null,
    val status: MessageStatus? = null
) {

    val timeStampDate: LocalDateTime
        get() {
            val instant = Instant.ofEpochMilli(timeStamp)
            return LocalDateTime.ofInstant(instant, ZoneId.of(DEFAULT_DATE_ZONE))
        }

}
