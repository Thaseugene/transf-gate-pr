package com.trans.integration.tg

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.trans.plugins.KafkaService
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.files.downloadFile
import dev.inmo.tgbotapi.extensions.api.files.downloadFileToTemp
import dev.inmo.tgbotapi.extensions.api.get.getFileAdditionalInfo
import dev.inmo.tgbotapi.extensions.api.send.*
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.*
import dev.inmo.tgbotapi.requests.abstracts.asMultipartFile
import dev.inmo.tgbotapi.types.actions.TypingAction
import dev.inmo.tgbotapi.types.media.TelegramMediaAudio
import dev.inmo.tgbotapi.types.media.TelegramMediaDocument
import dev.inmo.tgbotapi.types.media.TelegramMediaPhoto
import dev.inmo.tgbotapi.types.media.TelegramMediaVideo
import dev.inmo.tgbotapi.types.message.content.*
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
        botService.launchThis()
    }
}

class BotService(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val tgBot = telegramBot(System.getenv("botToken") ?: "default_value")

    private val logger: Logger = LoggerFactory.getLogger(BotService::class.java)

    suspend fun launchThis() {
        launchTgBotMessageConsuming()
    }

    companion object {

        val objectMapper: ObjectMapper = with(jacksonObjectMapper()) {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        }
    }

    private suspend fun launchTgBotMessageConsuming() {

        val directoryOrFile = File("/tmp/")
        directoryOrFile.mkdirs()
        tgBot.buildBehaviourWithLongPolling {
            onCommand("start") {
                println(objectMapper.writeValueAsString(it))
                reply(it, "Hello fucking shit")
            }
            onMedia(initialFilter = null) {
                logger.info(objectMapper.writeValueAsString(it))
                val content = it.content
                val pathedFile = bot.getFileAdditionalInfo(content.media)
                val outFile = File(directoryOrFile, pathedFile.filePath.filenameFromUrl)
                runCatching {
                    bot.downloadFile(content.media, outFile)
                }.onFailure {
                    it.printStackTrace()
                }.onSuccess { _ ->
                    withAction(it.chat.id, TypingAction) {
                        logger.info("Content type - $content")
                        when (content) {
                            is MediaGroupContent<*> -> replyWithMediaGroup(
                                it,
                                content.group.map {
                                    when (val innerContent = it.content) {
                                        is AudioContent -> TelegramMediaAudio(
                                            downloadFileToTemp(innerContent.media).asMultipartFile()
                                        )
                                        is DocumentContent -> TODO()
                                        is PhotoContent -> TODO()
                                        is VideoContent -> TODO()
                                    }
                                }
                            )
                            is VoiceContent, is AudioContent -> replyWithAudio(
                                it,
                                outFile.asMultipartFile()
                            )
                            else  -> reply(
                                it,
                                "Incorrect file type, please download voice or audio file"
                            )
                        }
                    }
                }
            }
        }.join()
    }
}
