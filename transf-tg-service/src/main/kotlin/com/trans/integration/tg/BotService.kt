package com.trans.integration.tg

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.trans.service.MessageService
import com.trans.service.MessageServiceImpl
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.get.getFileAdditionalInfo
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.api.send.withAction
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onMedia
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.MessageId
import dev.inmo.tgbotapi.types.RawChatId
import dev.inmo.tgbotapi.types.ReplyParameters
import dev.inmo.tgbotapi.types.actions.TypingAction
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.AudioContent
import dev.inmo.tgbotapi.types.message.content.MediaContent
import dev.inmo.tgbotapi.types.message.content.VoiceContent
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Application.configureBot() {

    val botService by inject<BotService>()
    val dispatcher by inject<CoroutineDispatcher>()

    CoroutineScope(dispatcher).launch {
        botService.prepareMessageListener()
    }
}

class BotService {

    private val messageService:MessageService by inject(MessageService::class.java)

    private val tgBot = telegramBot(System.getenv("botToken"))

    private val downloadFilePath = "https://api.telegram.org/file/bot%s/%s"

    private val logger: Logger = LoggerFactory.getLogger(BotService::class.java)

    suspend fun prepareMessageListener() {
        launchMessageListener()
    }

    companion object {
        val objectMapper: ObjectMapper = with(jacksonObjectMapper()) {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        }
    }

    private suspend fun launchMessageListener() {
        tgBot.buildBehaviourWithLongPolling {
            onCommand("start") {
                println(objectMapper.writeValueAsString(it))
                reply(it, "Please, send me some audio or voice message, and I'll make transcription =)")
            }
            onMedia(initialFilter = null) { commonMessage ->
                processMediaInput(commonMessage)
            }
            onText {
                reply(it, "Please, send me some audio or voice message, and I'll make transcription =)")
            }
        }.join()
    }

    private suspend fun BehaviourContext.processMediaInput(commonMessage: CommonMessage<MediaContent>) {
        logger.info("Received new bot message -> ${objectMapper.writeValueAsString(commonMessage)}")
        try {
            withAction(commonMessage.chat.id, TypingAction) {
                when (commonMessage.content) {
                    is VoiceContent, is AudioContent -> {
                        val pathFile = bot.getFileAdditionalInfo(commonMessage.content.media)
                        val preparedFilePath: String =
                            downloadFilePath.format(System.getenv("botToken"), pathFile.filePath)
                        messageService.processTelegramMessage(commonMessage, preparedFilePath)
                    }
                    else -> reply(
                        commonMessage,
                        "Incorrect file type, please upload voice or audio file"
                    )
                }
            }
        } catch (ex: Exception) {
            reply(commonMessage, "Something went wrong, please try later...")
        }

    }

    suspend fun sendAnswer(answer: String, chatId: Long, messageId: Long) {
        val replyParams = ReplyParameters(ChatId(RawChatId(chatId)), MessageId(messageId))
        tgBot.sendTextMessage(replyParams.chatIdentifier, answer, replyParameters = replyParams)
    }

}
