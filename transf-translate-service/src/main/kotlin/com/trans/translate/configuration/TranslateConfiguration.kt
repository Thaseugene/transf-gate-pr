package com.trans.translate.configuration

object TranslateConfiguration {

    val TRANSLATION_API_TOKEN: String = System.getenv("TRANSLATION_API_TOKEN") ?: "some-token"

    val API_URL: String = "https://api.lecto.ai/v1/translate/text"

}
