package com.troplo.privateuploader.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.flow.MutableStateFlow

@JsonClass(generateAdapter = true)
data class Chat(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "users") var users: List<ChatAssociation>,
    @field:Json(name = "recipient") val recipient: PartialUser?,
    @field:Json(name = "icon") val icon: String?,
    @field:Json(name = "type") val type: String?,
    @field:Json(name = "createdAt") val createdAt: String?,
    @field:Json(name = "updatedAt") val updatedAt: String?,
    @field:Json(name = "legacyUserId") val legacyUserId: String?,
    @field:Json(name = "user") val user: PartialUser?,
    @field:Json(name = "legacyUser") val legacyUser: User?,
    @field:Json(name = "association") val association: ChatAssociation?,
    @field:Json(name = "messages") val messages: List<Message>?,
    @field:Json(name = "unread") var unread: Int?,
    @field:Json(name = "typers") var typers: MutableStateFlow<List<Typing>>? = MutableStateFlow(
        emptyList()
    ),
)

@JsonClass(generateAdapter = true)
data class ChatCreateRequest(
    @field:Json(name = "users") val users: List<Int>,
)

@JsonClass(generateAdapter = true)
data class RemoveChatEvent(
    @field:Json(name = "id") val id: Int,
)

@JsonClass(generateAdapter = true)
data class RemoveChatUserEvent(
    // this refers to the user's associationId
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "chatId") val chatId: Int
)

@JsonClass(generateAdapter = true)
data class AddChatUsersEvent(
    @field:Json(name = "chatId") val chatId: Int,
    @field:Json(name = "users") val users: List<ChatAssociation>,
)