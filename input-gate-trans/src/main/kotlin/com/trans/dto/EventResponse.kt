package com.trans.dto

data class EventResponse(
    val id: Long,
    val clientId: String,
    val topicName: String,
    val requestId: String,
    val description: String,
    val timeStamp: Long,
    val eventName: String,
    val value: ByteArray? = null
)
