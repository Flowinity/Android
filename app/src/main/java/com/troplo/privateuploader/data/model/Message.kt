package com.troplo.privateuploader.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Message(
  @field:Json(name = "id") val id: Int,
  @field:Json(name = "chatId") val chatId: Int,
  @field:Json(name = "userId") val userId: Int,
  @field:Json(name = "content") val content: String,
  @field:Json(name = "type") val type: String,
  @field:Json(name = "embeds") var embeds: List<Embed>,
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
  @field:Json(name = "createdAt") val createdAt: Date?,
  @field:Json(name = "updatedAt") val updatedAt: Date?,
  @field:Json(name = "pinned") val pinned: Boolean?,
  @field:Json(name = "readReceipts") val readReceipts: List<ChatAssociation>
)


@JsonClass(generateAdapter = true)
data class MessageRequest(
  @field:Json(name = "content") val content: String,
  @field:Json(name = "attachments") val attachments: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class EditRequest(
  @field:Json(name = "content") val content: String,
  @field:Json(name = "id") val id: Int
)

@JsonClass(generateAdapter = true)
data class MessageEvent(
  @field:Json(name = "message") val message: Message,
  @field:Json(name = "mention") val mention: Boolean,
  @field:Json(name = "chat") val chat: Chat,
  @field:Json(name = "association") val association: ChatAssociation
)

@JsonClass(generateAdapter = true)
data class DeleteEvent(
  @field:Json(name = "chatId") val chatId: Int,
  @field:Json(name = "id") val id: Int
)

@JsonClass(generateAdapter = true)
data class EditEvent(
  @field:Json(name = "chatId") val chatId: Int,
  @field:Json(name = "id") val id: Int,
  @field:Json(name = "content") val content: String,
  @field:Json(name = "edited") val edited: Boolean,
  @field:Json(name = "editedAt") val editedAt: String?,
  @field:Json(name = "user") val user: User?
)

data class Embed(
  val type: String,
  val data: EmbedData?
)

data class EmbedData(
  val url: String?,
  val title: String?,
  val description: String?,
  val siteName: String?,
  val width: Int?,
  val height: Int?,
  val upload: Upload?
)

@JsonClass(generateAdapter = true)
data class EmbedResolutionEvent(
  @field:Json(name = "chatId") val chatId: Int,
  @field:Json(name = "id") val id: Int,
  @field:Json(name = "embeds") val embeds: List<Embed>
)

data class EmbedFail(
  val data: EmbedResolutionEvent,
  val retries: Int
)