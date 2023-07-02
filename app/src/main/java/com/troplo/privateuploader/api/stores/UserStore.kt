package com.troplo.privateuploader.api.stores

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.BuildConfig
import com.google.firebase.messaging.FirebaseMessaging
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.FCMTokenRequest
import com.troplo.privateuploader.data.model.Notification
import com.troplo.privateuploader.data.model.SettingsPayload
import com.troplo.privateuploader.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException


object UserStore {
    var user: MutableStateFlow<User?> = MutableStateFlow(null)
    var cachedUsers: MutableStateFlow<List<User>> = MutableStateFlow(listOf())
    var debug = BuildConfig.DEBUG

    fun registerFCMToken() {
        if (this.user.value == null) return
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("UserStore", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result ?: return@OnCompleteListener

            CoroutineScope(Dispatchers.IO).launch {
                TpuApi.retrofitService.registerFcmToken(FCMTokenRequest(token)).execute()
            }
        })
    }

    fun initializeUser(context: Context) {
        try {
            debug = SessionManager(context).getDebugMode()
            if (SessionManager(context).getUserCache() != null) {
                Log.d("TPU.Untagged", "User cache ${SessionManager(context).getUserCache()}")
                user.value = SessionManager(context).getUserCache()
            }
            CoroutineScope(
                Dispatchers.IO
            ).launch {
                user.value = TpuApi.retrofitService.getUser().execute().body()
                SessionManager(context).setUserCache(user.value)
                FriendStore.initializeFriends()
                CollectionStore.initializeCollections()

                if (user.value !== null) {
                    val token = SessionManager(context).getFCMToken()

                    if (token == null) {
                        registerFCMToken()
                    }
                }
            }

            val socket = SocketHandler.getSocket()

            socket?.on("userSettingsUpdate") {
                val jsonArray = it[0] as JSONObject
                val payload = jsonArray.toString()
                val settings = SocketHandler.gson.fromJson(payload, SettingsPayload::class.java)
                user.value = user.value?.copy(
                    description = settings.description ?: user.value?.description ?: "",
                    storedStatus = settings.storedStatus ?: user.value?.storedStatus ?: "",
                    email = settings.email ?: user.value?.email ?: "",
                    discordPrecache = settings.discordPrecache ?: user.value?.discordPrecache
                    ?: false,
                    itemsPerPage = settings.itemsPerPage ?: user.value?.itemsPerPage ?: 12,
                    language = settings.language ?: user.value?.language ?: "en",
                    excludedCollections = settings.excludedCollections
                        ?: user.value?.excludedCollections ?: listOf(),
                    insights = settings.insights ?: user.value?.insights ?: "friends"
                )
            }

            socket?.on("notification") {
                val jsonArray = it[0] as JSONObject
                val payload = jsonArray.toString()
                val notification = SocketHandler.gson.fromJson(payload, Notification::class.java)
                Log.d("UserStore", "Notification received: $notification")
                user.value?.notifications = user.value?.notifications?.plus(notification) ?: listOf(notification)
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun getUser(): User? {
        return user.value
    }

    fun logout(context: Context) {
        user.value = null
        val socket = SocketHandler.getSocket()
        socket?.off("userSettingsUpdate")
        socket?.off("notification")
        SessionManager(context).setUserCache(null)
        SessionManager(context).setFCMToken(null)
        SessionManager(context).saveAuthToken(null)
    }

    fun getUserProfile(username: String): Deferred<User?> {
        // TODO: Fix crash
        /*val cachedUser = cachedUsers.value.find { it.username == username }
        if (cachedUser != null) {
            return CompletableDeferred(cachedUser)
        }*/
        return CoroutineScope(Dispatchers.IO).async {
            val response = TpuApi.retrofitService.getUserProfile(username).execute()
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    cachedUsers.value = cachedUsers.value.plus(user)
                    return@async user
                }
            }
            return@async null
        }
    }
}