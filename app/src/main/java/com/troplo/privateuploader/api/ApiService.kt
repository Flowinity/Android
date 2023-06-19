package com.troplo.privateuploader.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

data class User(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "username") val username: String,
    @field:Json(name = "avatar") val avatar: String
)

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

@JsonClass(generateAdapter = true)
data class ChatAssociation(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "chatId") val chatId: Int,
    @field:Json(name = "userId") val userId: Int,
    @field:Json(name = "rank") val rank: String,
    @field:Json(name = "lastRead") val lastRead: Int,
    @field:Json(name = "notifications") val notifications: String,
    @field:Json(name = "legacyUserId") val legacyUserId: Int,
    @field:Json(name = "tpuUser") val tpuUser: User?,
    @field:Json(name = "legacyUser") val legacyUser: User?,
    @field:Json(name = "user") val user: User
)

@JsonClass(generateAdapter = true)
data class Typing(
    @field:Json(name = "chatId") val chatId: Int,
    @field:Json(name = "userId") val userId: Int,
    @field:Json(name = "user") val user: User,
    @field:Json(name = "expires") val expires: String
)

@JsonClass(generateAdapter = true)
data class Chat(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "users") val users: List<User>,
    @field:Json(name = "recipient") val recipient: User?,
    @field:Json(name = "icon") val icon: String?,
    @field:Json(name = "type") val type: String?,
    @field:Json(name = "createdAt") val createdAt: String?,
    @field:Json(name = "updatedAt") val updatedAt: String?,
    @field:Json(name = "legacyUserId") val legacyUserId: String?,
    @field:Json(name = "user") val user: User?,
    @field:Json(name = "legacyUser") val legacyUser: User?,
    @field:Json(name = "association") val association: ChatAssociation?,
    @field:Json(name = "messages") val messages: List<Message>?,
    @field:Json(name = "unread") val unread: String?,
    @field:Json(name = "typers") val typers: List<Typing>?
)


@JsonClass(generateAdapter = true)
data class LoginRequest(
    @field:Json(name = "username") val username: String,
    @field:Json(name = "password") val password: String,
    @field:Json(name = "code") val code: String
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @field:Json(name = "token") val token: String,
    @field:Json(name = "user") val user: User
)

private const val BASE_URL = "http://192.168.0.12:34582/api/v3/"
val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        // add Authorization header
        level = HttpLoggingInterceptor.Level.BODY

    })
    .build()

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()


interface TpuApiService {
    @GET("chats")
    fun getChats(
        @Header("Authorization") token: String
    ): Call<List<Chat>>

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("user")
    fun getUser(
        @Header("Authorization") token: String
    ): Call<User>
}

object TpuApi {
    val retrofitService: TpuApiService by lazy {
        retrofit.create(TpuApiService::class.java)
    }
}