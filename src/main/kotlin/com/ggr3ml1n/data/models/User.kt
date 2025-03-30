package com.ggr3ml1n.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val login: String?,
    val password: String?
) {
    fun isNullOrEmpty(): Boolean = login.isNullOrEmpty() || password.isNullOrEmpty()
}
