package com.trans.domain

import com.trans.persistanse.entity.MessageStatus
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

const val DEFAULT_DATE_ZONE = "UTC"

@Serializable
data class MessageModel(
    val id: Long,
    val user: UserModel,
    val requestId: String,
    val chatId: String,
    val messageId: String,
    val timeStamp: Long,
    val messageValue: ByteArray? = null,
    val messageResult: ByteArray? = null,
    val status: MessageStatus
) {

    val timeStampDate: LocalDateTime
        get() {
            val instant = Instant.ofEpochMilli(timeStamp)
            return LocalDateTime.ofInstant(instant, ZoneId.of(DEFAULT_DATE_ZONE))
        }

}
