package com.troplo.privateuploader.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Gallery(
  @field:Json(name = "gallery") val gallery: List<Upload>,
  @field:Json(name = "pager") val pager: Pager
)

@JsonClass(generateAdapter = true)
data class Pager(
  @field:Json(name = "page") val page: Int,
  @field:Json(name = "total") val total: Int,
  @field:Json(name = "limit") val limit: Int
)