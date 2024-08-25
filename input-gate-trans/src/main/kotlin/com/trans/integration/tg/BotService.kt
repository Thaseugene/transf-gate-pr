package com.trans.integration.tg

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.trans.integration.transacription.TranscriptionService
import com.trans.service.MessageService
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.files.downloadFile
import dev.inmo.tgbotapi.extensions.api.get.getFileAdditionalInfo
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.api.send.withAction
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onMedia
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.MessageId
import dev.inmo.tgbotapi.types.RawChatId
import dev.inmo.tgbotapi.types.ReplyParameters
import dev.inmo.tgbotapi.types.actions.TypingAction
import dev.inmo.tgbotapi.types.message.content.AudioContent
import dev.inmo.tgbotapi.types.message.content.VoiceContent
import dev.inmo.tgbotapi.utils.filenameFromUrl
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

fun Application.configureBot() {

    val botService by inject<BotService>()
    val dispatcher by inject<CoroutineDispatcher>()

    CoroutineScope(dispatcher).launch {
        botService.prepareMessageListener()
    }
}

class BotService(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val transcriptionService: TranscriptionService,
    private val messageService: MessageService
) {

    private val tgBot = telegramBot(System.getenv("botToken") ?: "default_value")

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

        val directoryOrFile = File("/tmp/")
        directoryOrFile.mkdirs()
        tgBot.buildBehaviourWithLongPolling {
            onCommand("start") {
                println(objectMapper.writeValueAsString(it))
                reply(it, "Please, send me some audio or voice message, and I'll make transcription =)")
            }
            onMedia(initialFilter = null) { commonMessage ->

                logger.info(objectMapper.writeValueAsString(commonMessage))
                val content = commonMessage.content
                val pathedFile = bot.getFileAdditionalInfo(content.media)
                try {
                } catch (e: Exception) {
                    logger.error("Problem", e)
                    bot.reply(commonMessage, "Error while trying to get file from chat, please try later...")
                    return@onMedia
                }
                logger.info("Full file path for this file -> ${pathedFile.filePath}")
                val outFile = File(directoryOrFile, pathedFile.filePath.filenameFromUrl)
                runCatching {
                    bot.downloadFile(content.media, outFile)
                }.onFailure {
                    logger.error("Error while trying to get file from chat", it)
                    reply(commonMessage, "Error while trying to get file from chat, please try later...")
                }.onSuccess { _ ->
                    val message = messageService.processIncomingMessage(commonMessage, outFile.readBytes())
                    withAction(commonMessage.chat.id, TypingAction) {
                        logger.info("Content type - $content")
                        when (content) {
                            is VoiceContent, is AudioContent -> message?.id?.let { messageId ->
                                transcriptionService.tryToMakeTranscript(messageId)
                                    ?.let { sendAnswer(it, message.chatId, message.messageId) }
                            }
                            else  -> reply(
                                commonMessage,
                                "Incorrect file type, please download voice or audio file"
                            )
                        }
                    }
                }
            }
            onText {
                reply(it, "Please, send me some audio or voice message, and I'll make transcription =)")
            }
        }.join()
    }


    suspend fun sendAnswer(answer: String, chatId: Long, messageId: Long) {
        val chat = ChatId(RawChatId(chatId))
        val replyParams = ReplyParameters(chat, MessageId(messageId))
        tgBot.sendTextMessage(chat, answer, replyParameters = replyParams)
    }
}
