package com.trans.service.mapping

import storage.trans.com.domain.UserModel
import storage.trans.com.persistance.entity.UserEntity

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

