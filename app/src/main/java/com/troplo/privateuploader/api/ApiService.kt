package com.troplo.privateuploader.api

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.troplo.privateuploader.BuildConfig
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.data.model.Chat
import com.troplo.privateuploader.data.model.ChatCreateRequest
import com.troplo.privateuploader.data.model.EditRequest
import com.troplo.privateuploader.data.model.FCMTokenRequest
import com.troplo.privateuploader.data.model.Friend
import com.troplo.privateuploader.data.model.Gallery
import com.troplo.privateuploader.data.model.LoginRequest
import com.troplo.privateuploader.data.model.LoginResponse
import com.troplo.privateuploader.data.model.Message
import com.troplo.privateuploader.data.model.MessageRequest
import com.troplo.privateuploader.data.model.MessageSearchResponse
import com.troplo.privateuploader.data.model.PatchUser
import com.troplo.privateuploader.data.model.StarResponse
import com.troplo.privateuploader.data.model.State
import com.troplo.privateuploader.data.model.UploadResponse
import com.troplo.privateuploader.data.model.User
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.http2.ConnectionShutdownException
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException

object TpuApi {
    private lateinit var token: String
    private lateinit var client: OkHttpClient
    private lateinit var retrofit: Retrofit
    private var baseUrl = "${BuildConfig.SERVER_URL}/api/v3/"
    var instance = BuildConfig.SERVER_URL

    fun init(token: String, context: Context) {
        this.token = token
        client = OkHttpClient.Builder()
            .addInterceptor(ErrorHandlingInterceptor(context))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(AuthorizationInterceptor())
            .addInterceptor(hostInterceptor)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // replace BuildConfig.SERVER_URL with baseUrl for multiple TPU instances
    private val hostInterceptor = Interceptor { chain ->
        val url = URL(instance)
        val port = if(url.port == -1) url.defaultPort else url.port
        val scheme = url.protocol
        val host = url.host

        val request = chain.request()
        val newUrl = request.url.newBuilder()
            .host(host)
            .scheme(scheme)
            .port(port)
            .build()
        val newRequest = request.newBuilder()
            .url(newUrl)
            .build()
        chain.proceed(newRequest)
    }


    private class ErrorHandlingInterceptor(private val context: Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            try {
                val response = chain.proceed(request)

                if (!response.isSuccessful) {
                    val error: JSONObject = JSONObject(response.body?.string() ?: "{}")
                    val errorType = error.getJSONArray("errors").getJSONObject(0).getString("name")
                    if(errorType == "INVALID_TOKEN") {
                        UserStore.logout(context)
                    } else {
                        val errorMessage =
                            error.getJSONArray("errors").getJSONObject(0).getString("message")
                        showToast(errorMessage)
                    }
                    return Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_2)
                        .code(999)
                        .message("Error")
                        .body("TpuServerError".toResponseBody(null)).build()
                }

                return response
            } catch (e: Exception) {
                e.printStackTrace()
                var msg = ""
                when (e) {
                    is SocketTimeoutException -> {
                        msg = "Timeout - Please check your internet connection."
                    }

                    is UnknownHostException -> {
                        msg = "Unable to make a connection. Please check your internet connection."
                    }

                    is ConnectionShutdownException -> {
                        msg = "Connection shutdown. Please check your internet connection."
                    }

                    is IOException -> {
                        msg = "TPU Server is unreachable, please try again later."
                    }

                    is IllegalStateException -> {
                        msg = "${e.message}"
                    }

                    else -> {
                        msg = "${e.message}"
                    }
                }
                showToast(msg)
                return Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_2)
                    .code(999)
                    .message(msg)
                    .body("{${e}}".toResponseBody(null)).build()
            }
        }

        private fun showToast(message: String) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private class AuthorizationInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .header("Authorization", token)
                .header("X-TPU-Client", "android_kotlin")
                .method(originalRequest.method, originalRequest.body)
            val request = requestBuilder.build()
            return chain.proceed(request)
        }
    }

    interface TpuApiService {
        @GET("chats")
        fun getChats(): Call<List<Chat>>

        @POST("auth/login")
        fun login(@Body request: LoginRequest): Call<LoginResponse>

        @GET("user")
        fun getUser(): Call<User>

        @GET("gallery")
        fun getGallery(
            @Query("page") page: Int = 1,
            @Query("search") search: String = "",
            @Query("textMetadata") textMetadata: Boolean = true,
            @Query("filter") filter: String = "all",
            @Query("sort") sort: String = "\"newest\"",
        ): Call<Gallery>

        @GET("chats/{id}/messages")
        fun getMessages(
            @Path("id") id: Int,
            @Query("offset") offset: Int? = null
        ): Call<List<Message>>

        @POST("chats/{id}/message")
        fun sendMessage(
            @Path("id") id: Int,
            @Body messageRequest: MessageRequest,
        ): Call<Message>

        @POST("gallery/star/{attachment}")
        fun star(
            @Path("attachment") attachment: String,
        ): Call<StarResponse>

        @PUT("chats/{chatId}/message")
        fun editMessage(
            @Path("chatId") chatId: Int,
            @Body editRequest: EditRequest,
        ): Call<Message>

        @DELETE("chats/{chatId}/messages/{messageId}")
        fun deleteMessage(
            @Path("chatId") chatId: Int,
            @Path("messageId") messageId: Int,
        ): Call<Unit>

        @GET("user/profile/{username}")
        fun getUserProfile(
            @Path("username") username: String
        ): Call<User>

        @GET("user/friends")
        fun getFriends(): Call<List<Friend>>

        @GET("chats/{chatId}/search")
        fun searchMessages(
            @Path("chatId") chatId: Int,
            @Query("query") query: String = "",
            @Query("page") page: Int = 1
        ): Call<MessageSearchResponse>

        @POST("chats")
        fun createChat(
            @Body members: ChatCreateRequest
        ): Call<Chat>

        @GET("/api/v3/core")
        fun getInstanceInfo(): Call<State>

        @POST("user/friends/username/{username}/{type}")
        fun addFriend(
            @Path("username") username: String,
            @Path("type") type: String
        ): Call<Unit>

        @POST("user/fcmToken")
        fun registerFcmToken(
            @Body token: FCMTokenRequest
        ): Call<Unit>

        @PATCH("user")
        fun updateUser(
            @Body user: PatchUser
        ): Call<Unit>

        @Multipart
        @POST("gallery")
        fun uploadFile(
            @Part attachment: MultipartBody.Part
        ): Call<UploadResponse>
    }

    val retrofitService: TpuApiService by lazy {
        retrofit.create(TpuApiService::class.java)
    }
}
