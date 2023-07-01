package com.troplo.privateuploader.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PartialCollection(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
)

data class Collection(
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "image")
    val image: String?,
    @Json(name = "items")
    val items: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "permissionsMetadata")
    // Rare cases where this is null
    val permissionsMetadata: PermissionsMetadata?,
    @Json(name = "preview")
    val preview: Preview?,
    @Json(name = "recipient")
    val recipient: Recipient,
    @Json(name = "shareLink")
    var shareLink: String?,
    @Json(name = "shared")
    val shared: Boolean,
    @Json(name = "updatedAt")
    val updatedAt: String,
    @Json(name = "user")
    val user: User,
    @Json(name = "userId")
    val userId: Int,
    @Json(name = "users")
    val users: List<CollectionUser>
)

data class PermissionsMetadata(
    @Json(name = "configure")
    val configure: Boolean,
    @Json(name = "read")
    val read: Boolean,
    @Json(name = "write")
    val write: Boolean
)

data class Preview(
    @Json(name = "attachment")
    val attachment: Attachment,
    @Json(name = "attachmentId")
    val attachmentId: Int,
    @Json(name = "collectionId")
    val collectionId: Int,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "identifier")
    val identifier: String,
    @Json(name = "pinned")
    val pinned: Boolean,
    @Json(name = "updatedAt")
    val updatedAt: String,
    @Json(name = "userId")
    val userId: Int
)

data class Recipient(
    @Json(name = "accepted")
    val accepted: Boolean,
    @Json(name = "collectionId")
    val collectionId: Int,
    @Json(name = "configure")
    val configure: Boolean,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "identifier")
    val identifier: String,
    @Json(name = "read")
    val read: Boolean,
    @Json(name = "recipientId")
    val recipientId: Int,
    @Json(name = "senderId")
    val senderId: Int,
    @Json(name = "updatedAt")
    val updatedAt: String,
    @Json(name = "write")
    val write: Boolean
)

data class CollectionUser(
    @Json(name = "accepted")
    val accepted: Boolean,
    @Json(name = "collectionId")
    val collectionId: Int,
    @Json(name = "configure")
    val configure: Boolean,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "identifier")
    val identifier: String,
    @Json(name = "read")
    val read: Boolean,
    @Json(name = "recipientId")
    val recipientId: Int,
    @Json(name = "senderId")
    val senderId: Int,
    @Json(name = "updatedAt")
    val updatedAt: String,
    @Json(name = "user")
    val user: PartialUser,
    @Json(name = "write")
    val write: Boolean
)

data class Attachment(
    @Json(name = "attachment")
    val attachment: String,
    @Json(name = "id")
    val id: Int
)

data class CollectivizeRequest(
    @Json(name = "attachmentId")
    val attachmentId: Int,
    @Json(name = "collectionId")
    val collectionId: Int
)

data class CreateCollectionRequest(
    @Json(name = "name")
    val name: String
)

data class ShareCollectionRequest(
    @Json(name = "id")
    val id: Int,
    @Json(name = "type")
    val type: String
)

data class ShareCollectionResponse(
    @Json(name = "shareLink")
    val shareLink: String
)

data class UpdateCollectionRequest(
    @Json(name = "name")
    val name: String
)