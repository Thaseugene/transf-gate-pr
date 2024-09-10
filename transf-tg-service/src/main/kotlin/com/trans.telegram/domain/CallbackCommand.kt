package com.trans.telegram.domain

data class CallbackCommand(
    val requestId: String,
    val command: CommandType
)

enum class CommandType {

    TRANSLATE,
    NOT_TRANSLATE

}
