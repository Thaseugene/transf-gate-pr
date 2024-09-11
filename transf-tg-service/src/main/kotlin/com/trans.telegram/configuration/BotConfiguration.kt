package com.trans.telegram.configuration

object BotConfiguration {

    val BOT_TOKEN: String = System.getenv("TG_BOT_TOKEN") ?: "some-token"

}
