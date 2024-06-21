package com.trans.domain

data class EventRecord(
    val id: Long = 0,
    val clientId: String = "",
    val topicName: String = "",
    val requestId: String = "",
    val description: String = "",
    val timeStamp: Long = 0,
    val eventName: String = "",
    val value: ByteArray = byteArrayOf(),
    val type: EventRecordExecuteType
)

enum class EventRecordExecuteType {
    CREATE,
    UPDATE,
    DELETE
}
