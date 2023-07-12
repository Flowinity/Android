package com.troplo.privateuploader.data.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class Typing(
    @field:Json(name = "chatId") val chatId: Int,
    @field:Json(name = "userId") val userId: Int,
    @field:Json(name = "user") val user: User,
    @field:Json(name = "expires") var expires: String,
)
