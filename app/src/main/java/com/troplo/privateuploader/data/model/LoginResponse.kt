package com.troplo.privateuploader.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
  @field:Json(name = "token") val token: String,
  @field:Json(name = "user") val user: User
)