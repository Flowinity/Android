package com.troplo.privateuploader.data.model

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class FCMTokenRequest(
    val token: String,
)