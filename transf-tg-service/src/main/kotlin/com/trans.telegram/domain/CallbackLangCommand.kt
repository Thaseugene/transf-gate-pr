package com.trans.telegram.domain

data class CallbackLangCommand(
    val requestId: String,
    val lang: String
)

enum class Language(s: String) {

    RUSSIAN ("ru"),
    ENGLISH ("en"),
    DEUCH ("de"),

}
