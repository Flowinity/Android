package com.troplo.privateuploader.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @field:Json(name = "errors") val errors: List<Error>,
)

@JsonClass(generateAdapter = true)
data class Error(
    @field:Json(name = "message") val message: String,
    @field:Json(name = "value") val value: String,
    @field:Json(name = "status") val status: String,
)