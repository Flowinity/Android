package com.troplo.privateuploader.data.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
@Keep
data class State(
    @Json(name = "announcements")
    val announcements: List<Announcement>,
    @Json(name = "connection")
    val connection: Connection,
    @Json(name = "domain")
    val domain: String,
    @Json(name = "features")
    val features: Features,
    @Json(name = "finishedSetup")
    val finishedSetup: Boolean,
    @Json(name = "hostname")
    val hostname: String,
    @Json(name = "hostnameWithProtocol")
    val hostnameWithProtocol: String,
    @Json(name = "hostnames")
    val hostnames: List<String>,
    @Json(name = "inviteAFriend")
    val inviteAFriend: Boolean,
    @Json(name = "maintenance")
    val maintenance: Boolean,
    @Json(name = "name")
    val name: String,
    @Json(name = "officialInstance")
    val officialInstance: Boolean,
    @Json(name = "preTrustedDomains")
    val preTrustedDomains: List<String>,
    @Json(name = "privacyNoteId")
    val privacyNoteId: String,
    @Json(name = "providers")
    val providers: Providers,
    @Json(name = "_redis")
    val redis: String,
    @Json(name = "registrations")
    val registrations: Boolean,
    @Json(name = "release")
    val release: String,
    @Json(name = "server")
    val server: String,
    @Json(name = "stats")
    val stats: Stats,
    @Json(name = "termsNoteId")
    val termsNoteId: String,
)

@JsonClass(generateAdapter = true)
@Keep
data class Announcement(
    @Json(name = "content")
    val content: String,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "type")
    val type: String,
    @Json(name = "updatedAt")
    val updatedAt: String,
    @Json(name = "user")
    val user: User,
    @Json(name = "userId")
    val userId: Int,
)

@JsonClass(generateAdapter = true)
@Keep
data class Connection(
    @Json(name = "ip")
    val ip: String,
    @Json(name = "whitelist")
    val whitelist: Boolean,
)

@JsonClass(generateAdapter = true)
@Keep
data class Features(
    @Json(name = "autoCollects")
    val autoCollects: Boolean,
    @Json(name = "collections")
    val collections: Boolean,
    @Json(name = "communications")
    val communications: Boolean,
    @Json(name = "insights")
    val insights: Boolean,
    @Json(name = "workspaces")
    val workspaces: Boolean,
)

@JsonClass(generateAdapter = true)
@Keep
data class Providers(
    @Json(name = "anilist")
    val anilist: Boolean,
    @Json(name = "lastfm")
    val lastfm: Boolean,
    @Json(name = "mal")
    val mal: Boolean,
)

@JsonClass(generateAdapter = true)
@Keep
data class Stats(
    @Json(name = "announcements")
    val announcements: Int,
    @Json(name = "chats")
    val chats: Int,
    @Json(name = "collectionItems")
    val collectionItems: Int,
    @Json(name = "collections")
    val collections: Int,
    @Json(name = "docs")
    val docs: Int,
    @Json(name = "inviteMilestone")
    val inviteMilestone: Int,
    @Json(name = "invites")
    val invites: Int,
    @Json(name = "messageGraph")
    val messageGraph: MessageGraph,
    @Json(name = "messages")
    val messages: Int,
    @Json(name = "pulse")
    val pulse: Int,
    @Json(name = "pulseGraph")
    val pulseGraph: PulseGraph,
    @Json(name = "pulses")
    val pulses: Int,
    @Json(name = "uploadGraph")
    val uploadGraph: UploadGraph,
    @Json(name = "uploads")
    val uploads: Int,
    @Json(name = "usage")
    val usage: Long,
    @Json(name = "usagePercentage")
    val usagePercentage: Double,
    @Json(name = "users")
    val users: Int,
)

@JsonClass(generateAdapter = true)
@Keep
data class MessageGraph(
    @Json(name = "data")
    val `data`: List<Int>,
    @Json(name = "labels")
    val labels: List<String>,
)

@JsonClass(generateAdapter = true)
@Keep
data class PulseGraph(
    @Json(name = "data")
    val `data`: List<Double>,
    @Json(name = "labels")
    val labels: List<String>,
)

@JsonClass(generateAdapter = true)
@Keep
data class UploadGraph(
    @Json(name = "data")
    val `data`: List<Int>,
    @Json(name = "labels")
    val labels: List<String>,
)