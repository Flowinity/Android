package com.troplo.privateuploader.data.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class CollectionItem(
    @Json(name = "id") val id: Int,
    @Json(name = "collectionId") val collectionId: Int,
    @Json(name = "attachmentId") val attachmentId: Int,
    @Json(name = "userId") val userId: Int,
    @Json(name = "identifier") val identifier: String,
    @Json(name = "pinned") val pinned: Boolean,
    @Json(name = "createdAt") val createdAt: String,
    @Json(name = "updatedAt") val updatedAt: String,
)