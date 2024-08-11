package com.trans.service.mapping

import com.trans.domain.UserModel
import com.trans.persistanse.entity.UserEntity

fun UserEntity.toUserModel() = UserModel(
    userId = this.id.value,
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
