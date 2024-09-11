package com.trans.telegram.model

data class CallbackLangCommand(
    val requestId: String,
    val lang: String
)

enum class Language(val lang: String) {

    RUSSIAN ("ru"),
    ENGLISH ("en"),
    DEUCH ("de"),

}
