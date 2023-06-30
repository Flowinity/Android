package com.troplo.privateuploader.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FCMTokenRequest(
    val token: String,
)