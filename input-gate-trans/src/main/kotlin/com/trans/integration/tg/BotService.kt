package com.trans.integration.tg

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.trans.domain.EventModel
import com.trans.integration.transacription.TranscriptionService
import com.trans.plugins.KafkaService
import com.trans.service.EventService
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
import java.util.UUID

fun Application.configureBot() {

    val botService by inject<BotService>()
    val dispatcher by inject<CoroutineDispatcher>()

    CoroutineScope(dispatcher).launch {
        botService.launchThis()
    }
}

class BotService(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val transcriptionService: TranscriptionService,
    private val eventService: EventService
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
                }.onSuccess { action ->
                    val event = eventService.createEvent(EventModel(
                        1L,
                        it.chat.id.chatId.toString(),
                        "test",
                        UUID.randomUUID().toString(),
                        "test",
                        System.currentTimeMillis(),
                        "test event",
                        outFile.readBytes()
                    ))
                    withAction(it.chat.id, TypingAction) {
                        logger.info("Content type - $content")
                        when (content) {
                            is VoiceContent, is AudioContent -> transcriptionService.tryToMakeTranscript(event.id.toString())
                                ?.let { it1 ->
                                    reply(it,
                                        it1
                                    )
                                }
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
