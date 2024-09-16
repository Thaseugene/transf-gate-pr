package com.trans.telegram.service

import com.transf.kafka.messaging.service.HandlerProvider
import com.transf.kafka.messaging.service.MessageHandler
import com.transf.kafka.messaging.service.type.HandlerType

class HandlerProviderImpl(
    private val handlers: List<MessageHandler<Any>>
) : HandlerProvider {

    private val handlersMap = handlers.associateBy { it.getType() }

    override fun retrieveHandler(type: HandlerType): Any? = handlersMap[type]

}

