package com.trans.telegram.configuration

import com.trans.telegram.service.tg.BotService
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject

object BotConfiguration {

    val BOT_TOKEN: String = System.getenv("TG_BOT_TOKEN") ?: "some-token"

    val DOWNLOAD_PATH = "https://api.telegram.org/file/bot$BOT_TOKEN/%s"

    val TG_BOT = telegramBot(BotConfiguration.BOT_TOKEN)

}

fun Application.configureBot() {

    val dispatcher by inject<CoroutineDispatcher>()
    val botService by inject<BotService>()

    CoroutineScope(dispatcher).launch {
        botService.prepareMessageListener()
    }
}
