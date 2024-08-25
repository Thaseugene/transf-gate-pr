package com.trans.service.mapping

import com.trans.domain.UserModel
import com.trans.persistanse.entity.UserEntity
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.MediaContent
import dev.inmo.tgbotapi.utils.RiskFeature

fun UserEntity.toUserModel() = UserModel(
    id = this.id.value,
    userId = this.userId,
    userName = this.userName,
    firstName = this.firstName,
    lastName = this.lastName
)

fun UserEntity.updateFields(userModel: UserModel): UserEntity {
    this.userName = userModel.userName
    this.firstName = userModel.firstName
    this.lastName = userModel.lastName
    return this
}


fun UserModel.toNewEntity(): UserEntity {
    val userModel = this
    return UserEntity.new {
        userName = userModel.userName
        firstName = userModel.firstName
        lastName = userModel.lastName
    }
}

fun ContentMessage<MediaContent>.toNewUser() = UserModel(
    1L,
    this.chat.id.chatId.long,
    this.from?.username?.full,
    this.from?.firstName,
    this.from?.lastName
)
