package com.troplo.privateuploader.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Upload(
    @Json(name = "id") val id: Int,
    @Json(name = "attachment") val attachment: String,
    @Json(name = "userId") val userId: Int,
    @Json(name = "name") val name: String,
    @Json(name = "originalFilename") val originalFilename: String,
    @Json(name = "type") val type: String,
    @Json(name = "urlRedirect") val urlRedirect: String?,
    @Json(name = "fileSize") val fileSize: Int,
    @Json(name = "deletable") val deletable: Boolean,
    @Json(name = "data") val data: Any?,
    @Json(name = "textMetadata") val textMetadata: String,
    @Json(name = "createdAt") val createdAt: Date,
    @Json(name = "updatedAt") val updatedAt: String,
    @Json(name = "starred") var starred: Star?,
    @Json(name = "collections") val collections: List<Collection>,
    @Json(name = "user") val user: User
)

@JsonClass(generateAdapter = true)
data class StarResponse(
    @Json(name = "status") val status: Boolean,
    @Json(name = "star") val star: Star?
)

@JsonClass(generateAdapter = true)
data class Star(
    @Json(name = "id") val id: Int,
    @Json(name = "userId") val userId: Int,
    @Json(name = "attachmentId") val attachmentId: Int,
    @Json(name = "createdAt") val createdAt: String,
    @Json(name = "updatedAt") val updatedAt: String
)