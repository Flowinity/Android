package com.troplo.privateuploader.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Message(
  @field:Json(name = "id") val id: Int,
  @field:Json(name = "chatId") val chatId: Int,
  @field:Json(name = "userId") val userId: Int,
  @field:Json(name = "content") val content: String,
  @field:Json(name = "type") val type: String,
  @field:Json(name = "embeds") val embeds: List<Any>,
  @field:Json(name = "edited") val edited: Boolean,
  @field:Json(name = "editedAt") val editedAt: String?,
  @field:Json(name = "replyId") val replyId: Int?,
  @field:Json(name = "legacyUserId") val legacyUserId: Int?,
  @field:Json(name = "tpuUser") val tpuUser: User?,
  @field:Json(name = "reply") val reply: Message?,
  @field:Json(name = "legacyUser") val legacyUser: User?,
  @field:Json(name = "user") val user: User?,
  @field:Json(name = "pending") val pending: Boolean?,
  @field:Json(name = "error") val error: Boolean?,
  @field:Json(name = "createdAt") val createdAt: String?,
  @field:Json(name = "updatedAt") val updatedAt: String?,
  @field:Json(name = "pinned") val pinned: Boolean?,
  @field:Json(name = "readReceipts") val readReceipts: List<ChatAssociation>
)
