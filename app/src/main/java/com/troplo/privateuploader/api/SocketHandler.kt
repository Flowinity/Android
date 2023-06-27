package com.troplo.privateuploader.api

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.troplo.privateuploader.BuildConfig
import com.troplo.privateuploader.data.model.MessageEvent
import com.troplo.privateuploader.data.model.Typing
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.Collections
import java.util.concurrent.Executors

object SocketHandler {
    private const val SERVER_URL = BuildConfig.SERVER_URL

    private var socket: Socket? = null
    val gson = Gson()
    var connected = mutableStateOf(false)

    fun initializeSocket(token: String, context: Context) {
        try {
            val options = IO.Options()
            options.forceNew = true
            options.reconnection = true
            options.auth = Collections.singletonMap("token", token)
            options.query = "platform=android_kotlin"
            options.transports = arrayOf("websocket")
            socket = IO.socket(SERVER_URL, options)
            if (socket != null) {
                socket?.open()
                socket?.on(Socket.EVENT_CONNECT) {
                    this.connected.value = true
                    println("Socket connected ${socket?.isActive}, Connected: ${this.connected.value}")
                }
                socket?.on(Socket.EVENT_DISCONNECT) {
                    this.connected.value = false
                    println("Socket disconnected ${socket?.isActive}, Connected: ${this.connected.value}")
                }
                socket?.on(Socket.EVENT_CONNECT_ERROR) {
                    try {
                        this.connected.value = false
                        println("Socket connect error ${socket?.isActive}, Connected: ${this.connected.value}, Error: ${it[0]}")
                    } catch (e: Exception) {
                        //
                    }
                }
                socket?.on("message") { it ->
                    val jsonArray = it[0] as JSONObject
                    val payload = jsonArray.toString()
                    val messageEvent = gson.fromJson(payload, MessageEvent::class.java)

                    val message = messageEvent.message
                    println("Message received (SocketHandler): $message")
                    if (messageEvent.association.id != ChatStore.associationId.value) {
                        // increase unread count
                        val chat =
                            ChatStore.chats.value.find { it.association?.id == messageEvent.association.id }
                        println(chat)
                        if (chat != null) {
                            chat.unread = chat.unread?.plus(1)
                            ChatStore.setChats(listOf(chat) + ChatStore.chats.value.filter { it.association?.id != messageEvent.association.id })
                        }
                    }
                }
                socket?.on("typing") { it ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val jsonArray = it[0] as JSONObject
                        val payload = jsonArray.toString()
                        val typeEvent = gson.fromJson(payload, Typing::class.java)
                        println("TYPING EVENT: $typeEvent")
                        val find = ChatStore.typers.value.find { it.userId == typeEvent.userId }
                        if (find == null) {
                            ChatStore.typers.value = ChatStore.typers.value + typeEvent
                        } else {
                            ChatStore.typers.value =
                                ChatStore.typers.value.filter { it.userId != typeEvent.userId } + typeEvent
                        }

                        // Remove the event
                        val scheduler = Executors.newSingleThreadScheduledExecutor()
                        scheduler.schedule({
                            if (ChatStore.typers.value.find { it.userId == typeEvent.userId }?.expires == typeEvent.expires) {
                                ChatStore.typers.value =
                                    ChatStore.typers.value.filter { it.userId != typeEvent.userId }
                            }
                        }, 5, java.util.concurrent.TimeUnit.SECONDS)
                    }
                }
                /*socket?.on("message") {
                  val jsonArray = it[0] as JSONObject
                  val payload = jsonArray.toString()
                  val messageEvent = gson.fromJson(payload, MessageEvent::class.java)

                  val message = messageEvent.message

                  val notificationBuilder = NotificationCompat.Builder(context, "communications")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(message.user?.username)
                    .setContentText(message.content)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(null)

                  val remoteInput: RemoteInput = RemoteInput.Builder("reply").run {
                    setLabel("Reply")
                    build()
                  }

                /*  val replyPendingIntent: PendingIntent =
                    PendingIntent.getBroadcast(
                      context,
                      messageEvent.association.id,
                      getMessageReplyIntent(messageEvent.association.id),
                      PendingIntent.FLAG_MUTABLE
                    )

                  val action: NotificationCompat.Action =
                    NotificationCompat.Action.Builder(
                      null,
                      "Reply",
                      replyPendingIntent
                    )
                      .addRemoteInput(remoteInput)
                      .build()
        */
                  val newMessageNotification = Notification.Builder(context, "communications")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(message.user?.username)
                    .setContentText(message.content)
                    .build()
                  val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(message.id, newMessageNotification)
                }*/
                println("Socket connected ${socket?.isActive}")
            } else {
                println("Socket is null")
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private fun getMessageReplyIntent(associationId: Int): Intent {
        return Intent()
            .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            .setAction("reply")
            .putExtra("associationId", associationId)
    }


    fun getSocket(): Socket? {
        return socket
    }

    fun closeSocket() {
        socket?.disconnect()
        socket = null
    }
}