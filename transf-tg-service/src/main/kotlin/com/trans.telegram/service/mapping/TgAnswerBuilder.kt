package com.trans.telegram.service.mapping

import com.trans.telegram.model.CallbackCommandType
import com.trans.telegram.model.LanguageType
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton

fun prepareCallBackTranslateCommands(previousRequestId: String): List<CallbackDataInlineKeyboardButton> {
    return listOf(
        CallbackDataInlineKeyboardButton(
            "Yes", "${CallbackCommandType.TRANSLATE}:$previousRequestId"
        ), CallbackDataInlineKeyboardButton(
            "No", "${CallbackCommandType.NOT_TRANSLATE}:$previousRequestId"
        )
    )
}

fun prepareCallBackLanguageCommands(previousRequestId: String): List<CallbackDataInlineKeyboardButton> {
    return LanguageType.entries.map { lang ->
        CallbackDataInlineKeyboardButton(
            lang.name, "${CallbackCommandType.LANG}:${lang.shortcut}:$previousRequestId"
        )
    }.toList()
}
