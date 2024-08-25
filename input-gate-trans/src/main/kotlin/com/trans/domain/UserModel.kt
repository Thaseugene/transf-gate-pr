package com.trans.domain

data class UserModel(
    val id: Long,
    val userId: Long,
    val userName: String? = null,
    var firstName: String? = null,
    var lastName: String? = null
)
