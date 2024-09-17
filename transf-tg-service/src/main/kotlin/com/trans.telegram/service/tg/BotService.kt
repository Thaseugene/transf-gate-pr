package com.trans.telegram.service.tg

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.answers.answer
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onDataCallbackQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onMedia
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText

class BotService(
    private val messageProcessor: BotMessageProcessor,
    private val bot: TelegramBot
) {

    suspend fun prepareMessageListener() {
        launchMessageListener()
    }

    private suspend fun launchMessageListener() {
        bot.buildBehaviourWithLongPolling {
            onCommand(START_COMMAND) {
                messageProcessor.processDefaultMessage(it)
            }
            onMedia(initialFilter = null) {
                messageProcessor.processMediaInput(it)
            }
            onText {
                messageProcessor.processDefaultMessage(it)
            }
            onDataCallbackQuery {
                answer(it, EMPTY_MESSAGE)
                messageProcessor.processCallBackMessages(it)
            }
        }.join()
    }

}
