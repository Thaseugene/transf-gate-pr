package com.trans.domain

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

private const val DEFAULT_DATE_ZONE = "UTC"

@Serializable
data class EventModel(
    val id: Long,
    val clientId: String,
    val topicName: String,
    val requestId: String,
    val description: String,
    val timeStamp: Long,
    val eventName: String,
    val value: ByteArray? = null
) {

    val timeStampDate: LocalDateTime
        get() {
            val instant = Instant.ofEpochMilli(timeStamp)
            return LocalDateTime.ofInstant(instant, ZoneId.of(DEFAULT_DATE_ZONE))
        }

}
