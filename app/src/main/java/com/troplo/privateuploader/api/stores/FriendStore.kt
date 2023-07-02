package com.troplo.privateuploader.api.stores

import android.util.Log
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.Friend
import com.troplo.privateuploader.data.model.FriendRequest
import com.troplo.privateuploader.data.model.StatusPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException


object FriendStore {
    var friends: MutableStateFlow<List<Friend>> = MutableStateFlow(listOf())

    fun initializeFriends() {
        try {
            val socket = SocketHandler.getSocket()
            socket?.off("userStatus")
            socket?.off("friendRequest")

            CoroutineScope(
                Dispatchers.IO
            ).launch {
                val response = TpuApi.retrofitService.getFriends().execute()
                if (response.isSuccessful) {
                    friends.value = response.body()!!
                }
            }


            socket?.on("userStatus") { it ->
                val jsonArray = it[0] as JSONObject
                val payload = jsonArray.toString()
                Log.d("TPU.Untagged", payload)
                val status = SocketHandler.gson.fromJson(payload, StatusPayload::class.java)
                val friend = friends.value.find { it.otherUser?.id == status.id }

                if (friend != null) {
                    friends.value = friends.value.minus(friend).plus(
                        friend.copy(
                            otherUser = friend.otherUser?.copy(
                                status = status.status ?: "offline",
                                platforms = status.platforms
                            )
                        )
                    )
                } else if (status.id == UserStore.user.value?.id) {
                    UserStore.user.value = UserStore.user.value?.copy(
                        status = status.status ?: "offline",
                        platforms = status.platforms
                    )
                }
            }

            socket?.on("friendRequest") { it ->
                val jsonArray = it[0] as JSONObject
                val payload = jsonArray.toString()
                val friend = SocketHandler.gson.fromJson(payload, FriendRequest::class.java)

                if ((friend.status == "incoming" || friend.status == "accepted" || friend.status == "outgoing") && friend.friend != null) {
                    val existingFriend =
                        friends.value.find { it.otherUser?.id == friend.friend.otherUser?.id }

                    if (existingFriend != null) {
                        friends.value = friends.value.minus(existingFriend).plus(friend.friend)
                    } else {
                        friends.value = friends.value.plus(friend.friend)
                    }
                } else if (friend.status == "removed") {
                    val existingFriend = friends.value.find { it.otherUser?.id == friend.id }

                    if (existingFriend != null) {
                        friends.value = friends.value.minus(existingFriend)
                    }
                }
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun updateFriendNickname(name: String, userId: Int) {
        friends.value = friends.value.map {
            if (it.otherUser?.id == userId) {
                it.copy(otherUser = it.otherUser?.copy(nickname = it.otherUser?.nickname?.copy(nickname = name)))
            } else {
                it
            }
        }
    }
}