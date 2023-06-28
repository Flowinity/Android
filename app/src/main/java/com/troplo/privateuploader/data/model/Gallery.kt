package com.troplo.privateuploader.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Gallery(
    @field:Json(name = "gallery") val gallery: List<Upload>,
    @field:Json(name = "pager") val pager: Pager,
)

@JsonClass(generateAdapter = true)
data class Pager(
    @field:Json(name = "currentPage") val currentPage: Int,
    @field:Json(name = "endIndex") val endIndex: Int,
    @field:Json(name = "endPage") val endPage: Int,
    @field:Json(name = "pageSize") val pageSize: Int,
    @field:Json(name = "pages") val pages: List<Int>,
    @field:Json(name = "startIndex") val startIndex: Int,
    @field:Json(name = "startPage") val startPage: Int,
    @field:Json(name = "totalItems") val totalItems: Int,
    @field:Json(name = "totalPages") val totalPages: Int
)