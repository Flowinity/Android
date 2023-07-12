package com.troplo.privateuploader.data.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class LoginResponse(
    @field:Json(name = "token") val token: String,
    @field:Json(name = "user") val user: User,
)