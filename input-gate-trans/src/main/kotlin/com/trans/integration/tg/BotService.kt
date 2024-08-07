package com.trans.integration.tg

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.telegramBotWithBehaviourAndLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject

fun Application.configureBot() {

    val botService by inject<BotService>()
    val dispatcher by inject<CoroutineDispatcher>()

    CoroutineScope(dispatcher).launch {
        botService.launchThis()
    }
}

class BotService(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun launchThis() {
        launchTgBotMessageConsuming()
    }

    companion object {

        val objectMapper: ObjectMapper = with(jacksonObjectMapper()) {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    private suspend fun launchTgBotMessageConsuming() {
        val botToken = System.getenv("botToken") ?: "default_value"
        telegramBotWithBehaviourAndLongPolling(botToken, CoroutineScope(dispatcher)) {
            println(botToken)
            onCommand("start") {
                println(objectMapper.writeValueAsString(it))
                reply(it, "Hello fucking shit")
            }

        }.second.join()
    }
}
