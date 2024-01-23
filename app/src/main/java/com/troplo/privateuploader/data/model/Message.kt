package com.troplo.privateuploader.data.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class Message(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "chatId") val chatId: Int,
    @field:Json(name = "userId") val userId: Int,
    @field:Json(name = "content") val content: String,
    // Type is null on Colubrina messages
    @field:Json(name = "type") val type: String?,
    @field:Json(name = "embeds") val embeds: List<EmbedDataV2>,
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
    @field:Json(name = "readReceipts") val readReceipts: List<ReadReceiptEvent>,
)


@JsonClass(generateAdapter = true)
@Keep
data class MessageRequest(
    @field:Json(name = "content") val content: String,
    @field:Json(name = "attachments") val attachments: List<String> = emptyList(),
    @field:Json(name = "replyId") val replyId: Int? = null,
)

@JsonClass(generateAdapter = true)
@Keep
data class EditRequest(
    @field:Json(name = "content") val content: String,
    @field:Json(name = "id") val id: Int,
)

@JsonClass(generateAdapter = true)
@Keep
data class MessageEvent(
    @field:Json(name = "message") val message: Message,
    @field:Json(name = "mention") val mention: Boolean,
    @field:Json(name = "chat") val chat: Chat,
    @field:Json(name = "associationId") val associationId: Number,
)

@JsonClass(generateAdapter = true)
@Keep
data class MessageEventFirebase(
    @field:Json(name = "content") val content: String,
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "associationId") val associationId: Int,
    @field:Json(name = "userId") val userId: Int,
    @field:Json(name = "username") val username: String,
    @field:Json(name = "avatar") val avatar: String,
    @field:Json(name = "chatName") val chatName: String,
    @field:Json(name = "createdAt") val createdAt: String,
    @field:Json(name = "type") val type: String,
)

@JsonClass(generateAdapter = true)
@Keep
data class DeleteEvent(
    @field:Json(name = "chatId") val chatId: Int,
    @field:Json(name = "id") val id: Int,
)

@JsonClass(generateAdapter = true)
@Keep
data class EditEvent(
    @field:Json(name = "chatId") val chatId: Int,
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "content") val content: String,
    @field:Json(name = "edited") val edited: Boolean,
    @field:Json(name = "editedAt") val editedAt: String?,
    @field:Json(name = "user") val user: User?,
    @field:Json(name = "pinned") val pinned: Boolean,
)

@JsonClass(generateAdapter = true)
@Keep
data class Embed(
    val type: String,
    val data: EmbedData?,
)

@JsonClass(generateAdapter = true)
@Keep
data class EmbedData(
    val url: String?,
    val title: String?,
    val description: String?,
    val siteName: String?,
    val width: Float?,
    val height: Float?,
    val upload: Upload?,
    val type: String?,
)

@JsonClass(generateAdapter = true)
@Keep
data class EmbedResolutionEvent(
    @field:Json(name = "chatId") val chatId: Int,
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "embeds") val embeds: List<EmbedDataV2>,
)

data class EmbedFail(
    val data: EmbedResolutionEvent,
    val retries: Int,
)

@JsonClass(generateAdapter = true)
@Keep
data class MessageSearchResponse(
    @field:Json(name = "messages") val messages: List<Message>,
    @field:Json(name = "pager") val pager: Pager,
)

@JsonClass(generateAdapter = true)
@Keep
data class MessagePaginate(
    @field:Json(name = "messages") val messages: List<Message>,
    @field:Json(name = "pager") val pager: Pager
)

@JsonClass(generateAdapter = true)
@Keep
data class PinRequest(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "pinned") val pinned: Boolean,
)

// EmbedDataV2
enum class EmbedMediaType {
    IMAGE,
    VIDEO,
    AUDIO,
    FILE
}

enum class EmbedVersion {
    COLUBRINA,
    V1,
    V2
}

@JsonClass(generateAdapter = true)
@Keep
data class EmbedMedia(
    @Json(name = "url") val url: String?,
    @Json(name = "proxyUrl") val proxyUrl: String?,
    @Json(name = "attachment") val attachment: String?,
    @Json(name = "width") val width: Int?,
    @Json(name = "height") val height: Int?,
    @Json(name = "isInternal") val isInternal: Boolean,
    @Json(name = "upload") val upload: Upload?,
    @Json(name = "mimeType") val mimeType: String?,
    @Json(name = "type") val type: Int,
    @Json(name = "videoEmbedUrl") val videoEmbedUrl: String?
)

@JsonClass(generateAdapter = true)
@Keep
data class EmbedText(
    @Json(name = "imageProxyUrl") val imageProxyUrl: String?,
    @Json(name = "text") val text: String,
    @Json(name = "heading") val heading: Boolean?,
    @Json(name = "imageUrl") val imageUrl: String?
)

enum class EmbedType {
    REGULAR,
    CHAT_INVITE,
    DIRECT
}

@JsonClass(generateAdapter = true)
@Keep
data class EmbedMetadata(
    @Json(name = "url") val url: String?,
    @Json(name = "siteName") val siteName: String?,
    @Json(name = "siteIcon") val siteIcon: String?,
    @Json(name = "footer") val footer: String?,
    @Json(name = "type") val type: Int,
    @Json(name = "id") val id: String?,
    @Json(name = "restricted") val restricted: Boolean = false
)

@JsonClass(generateAdapter = true)
@Keep
data class EmbedDataV2(
    @Json(name = "media") val media: List<EmbedMedia>?,
    @Json(name = "text") val text: List<EmbedText>?,
    @Json(name = "metadata") val metadata: EmbedMetadata,
    @Json(name = "version") val version: EmbedVersion = EmbedVersion.V2
)