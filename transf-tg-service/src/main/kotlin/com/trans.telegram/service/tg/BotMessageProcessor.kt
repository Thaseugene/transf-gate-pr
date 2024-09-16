package com.trans.telegram.service.tg

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.trans.telegram.configuration.BotConfiguration
import com.trans.telegram.model.CachedResponse
import com.trans.telegram.model.CallbackCommandType
import com.trans.telegram.service.MessageService
import com.trans.telegram.service.cache.CacheService
import com.trans.telegram.service.mapping.prepareCallBackLanguageCommands
import com.trans.telegram.service.mapping.prepareCallBackTranslateCommands
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.get.getFileAdditionalInfo
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.api.send.withAction
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.MessageId
import dev.inmo.tgbotapi.types.RawChatId
import dev.inmo.tgbotapi.types.ReplyParameters
import dev.inmo.tgbotapi.types.actions.TypingAction
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.AudioContent
import dev.inmo.tgbotapi.types.message.content.MediaContent
import dev.inmo.tgbotapi.types.message.content.MessageContent
import dev.inmo.tgbotapi.types.message.content.VoiceContent
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BotMessageProcessor(
    private val messageService: MessageService,
    private val cacheService: CacheService,
    private val bot: TelegramBot
) {

    private val logger: Logger = LoggerFactory.getLogger(BotMessageProcessor::class.java)

    companion object {
        val objectMapper: ObjectMapper = with(jacksonObjectMapper()) {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        }
    }

    suspend fun processCallBackMessages(dataCallbackQuery: DataCallbackQuery) {
        runCatching {
            val callBackResult = dataCallbackQuery.data.split(":");
            when (CallbackCommandType.valueOf(callBackResult.first())) {
                CallbackCommandType.TRANSLATE -> sendLangChooseMessage(
                    dataCallbackQuery.from.id,
                    callBackResult[1]
                )

                CallbackCommandType.NOT_TRANSLATE -> bot.send(dataCallbackQuery.from.id, "Okay =)")
                CallbackCommandType.LANG -> {
                    val lang = callBackResult[1]
                    val requestId = callBackResult[2]
                    if (dataCallbackQuery is MessageDataCallbackQuery) {
                        val cachedData = cacheService.retrieveCachedValue<CachedResponse>(requestId)
                        cachedData?.let {
                            if (it.translations.isNotEmpty()) {
                                val cachedResult = it.translations.first { translation -> translation.first == lang }
                                sendAnswer(cachedResult.second, it.chatId, it.messageId)
                                return@runCatching
                            }
                        }
                        messageService.processTranslateMessage(dataCallbackQuery, lang, requestId)
                    }
                }
            }
        }.onFailure {
            logger.error("Unexpected error while processing callback message", it)
            bot.send(dataCallbackQuery.from.id, INCORRECT_PROCESS_MESSAGE)
        }

    }

    suspend fun processMediaInput(commonMessage: CommonMessage<MediaContent>) {
        runCatching {
            logger.info("Received new bot message -> ${objectMapper.writeValueAsString(commonMessage)}")
            BotConfiguration.TG_BOT.withAction(commonMessage.chat.id, TypingAction) {
                when (commonMessage.content) {
                    is VoiceContent, is AudioContent -> {
                        val pathFile = BotConfiguration.TG_BOT.getFileAdditionalInfo(commonMessage.content.media)
                        val preparedFilePath: String =
                            BotConfiguration.DOWNLOAD_PATH.format(pathFile.filePath)
                        messageService.processTelegramMessage(commonMessage, preparedFilePath)
                    }

                    else -> reply(
                        commonMessage,
                        INCORRECT_FILE_TYPE_MESSAGE
                    )
                }
            }
        }.onFailure {
            logger.error("Error occurred while processing new telegram message", it)
            bot.reply(commonMessage, INCORRECT_PROCESS_MESSAGE)
        }
    }

    suspend fun processDefaultMessage(commonMessage: CommonMessage<MessageContent>) {
        bot.reply(commonMessage, DEFAULT_MESSAGE)
    }

    private suspend fun sendLangChooseMessage(
        chatId: ChatId,
        previousRequestId: String
    ) {
        bot.sendMessage(
            chatId,
            CHOOSE_LANGUAGE_MESSAGE,
            replyMarkup = InlineKeyboardMarkup(
                keyboard = listOf(prepareCallBackLanguageCommands(previousRequestId))
            )
        )
    }

    suspend fun sendErrorMessage(requestId: String) {
        cacheService.retrieveCachedValue<CachedResponse>(requestId)?.let {
            sendAnswer(INCORRECT_PROCESS_MESSAGE, it.chatId, it.messageId)
        }
    }

    suspend fun sendSuccessTranscriptMessage(
        answer: String,
        chatId: Long,
        messageId: Long,
        previousRequestId: String
    ) {
        sendAnswer(answer, chatId, messageId)
        cacheService.retrieveCachedValue<CachedResponse>(previousRequestId)?.let {
            it.transcriptResult = answer
            cacheService.swapCachedData(previousRequestId, it)
        }
        bot.sendMessage(
            ChatId(RawChatId(chatId)),
            TRANSLATION_ASK_MESSAGE,
            replyMarkup = InlineKeyboardMarkup(
                keyboard = listOf(prepareCallBackTranslateCommands(previousRequestId))
            )
        )
    }

    suspend fun sendSuccessTranslateMessage(
        answer: String,
        chatId: Long,
        messageId: Long,
        requestId: String,
        lang: String?
    ) {
        lang?.let { language ->
            cacheService.retrieveCachedValue<CachedResponse>(requestId)?.let {
                it.translations.toMutableList().also { list ->
                    list.add(language to answer)
                    it.translations = list
                }
                cacheService.swapCachedData(requestId, it)
            }
        }
        sendAnswer(answer, chatId, messageId)
    }

    private suspend fun sendAnswer(answer: String, chatId: Long, messageId: Long) {
        ReplyParameters(ChatId(RawChatId(chatId)), MessageId(messageId)).run {
            bot.send(chatIdentifier, answer, replyParameters = this)
        }
    }

}
