package com.troplo.privateuploader.api.stores

import android.content.Context
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.Friend
import com.troplo.privateuploader.data.model.MessageEvent
import com.troplo.privateuploader.data.model.StatusPayload
import com.troplo.privateuploader.data.model.User
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException


object FriendStore {
    var friends: MutableStateFlow<List<Friend>> = MutableStateFlow(listOf())

    fun initializeFriends() {
        try {
            CoroutineScope(
                Dispatchers.IO
            ).launch {
                friends.value = TpuApi.retrofitService.getFriends().execute().body()!!
            }

            val socket = SocketHandler.getSocket()

            socket?.on("userStatus") { it ->
                val jsonArray = it[0] as JSONObject
                val payload = jsonArray.toString()
                val status = SocketHandler.gson.fromJson(payload, StatusPayload::class.java)
                val friend = friends.value.find { it.otherUser?.id == status.id }

                if (friend != null) {
                    friends.value = friends.value.minus(friend).plus(
                        friend.copy(
                            otherUser = friend.otherUser?.copy(
                                status = status.status,
                                platforms = status.platforms
                            )
                        )
                    )
                } else if(status.id == UserStore.user.value?.id) {
                    UserStore.user.value = UserStore.user.value?.copy(
                        status = status.status,
                        platforms = status.platforms
                    )
                }
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }
}