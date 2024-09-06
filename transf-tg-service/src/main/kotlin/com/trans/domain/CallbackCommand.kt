package com.trans.domain

data class CallbackCommand(
    val requestId: String,
    val command: CommandType
)

enum class CommandType {

    TRANSLATE,
    NOT_TRANSLATE

}
