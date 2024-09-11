package com.trans.telegram.model

data class CallbackCommand(
    val requestId: String,
    val command: CommandType
)

enum class CommandType {

    TRANSLATE,
    NOT_TRANSLATE,
    LANG

}
