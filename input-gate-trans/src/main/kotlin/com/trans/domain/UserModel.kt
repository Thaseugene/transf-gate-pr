package com.trans.domain

import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val id: Long,
    val userName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null
)
