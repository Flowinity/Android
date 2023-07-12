package com.troplo.privateuploader.data.model


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class TenorResponse(
    @Json(name = "next")
    val next: String,
    @Json(name = "results")
    val results: List<Result>
)

@Keep
data class Result(
    @Json(name = "content_description")
    val contentDescription: String,
    @Json(name = "created")
    val created: Double,
    @Json(name = "flags")
    val flags: List<String>,
    @Json(name = "hasaudio")
    val hasaudio: Boolean,
    @Json(name = "id")
    val id: String,
    @Json(name = "itemurl")
    val itemurl: String,
    @Json(name = "media_formats")
    val media_formats: MediaFormats,
    @Json(name = "tags")
    val tags: List<String>,
    @Json(name = "title")
    val title: String,
    @Json(name = "url")
    val url: String
)

@Keep
data class MediaFormats(
    @Json(name = "gif")
    val gif: MediaType,
    @Json(name = "gifpreview")
    val gifpreview: MediaType,
    @Json(name = "loopedmp4")
    val loopedmp4: MediaType,
    @Json(name = "mediumgif")
    val mediumgif: MediaType,
    @Json(name = "mp4")
    val mp4: MediaType,
    @Json(name = "nanogif")
    val nanogif: MediaType,
    @Json(name = "nanogifpreview")
    val nanogifpreview: MediaType,
    @Json(name = "nanomp4")
    val nanomp4: MediaType,
    @Json(name = "nanowebm")
    val nanowebm: MediaType,
    @Json(name = "tinygif")
    val tinygif: MediaType,
    @Json(name = "tinygifpreview")
    val tinygifpreview: MediaType,
    @Json(name = "tinymp4")
    val tinymp4: MediaType,
    @Json(name = "tinywebm")
    val tinywebm: MediaType,
    @Json(name = "webm")
    val webm: MediaType
)

@Keep
data class MediaType(
    @Json(name = "dims")
    val dims: List<Int>,
    @Json(name = "duration")
    val duration: Float,
    @Json(name = "preview")
    val preview: String,
    @Json(name = "size")
    val size: Int,
    @Json(name = "url")
    val url: String
)