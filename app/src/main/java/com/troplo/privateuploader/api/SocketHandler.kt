package com.troplo.privateuploader.api

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reply
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat.getSystemService
import com.google.gson.Gson
import com.troplo.privateuploader.R
import com.troplo.privateuploader.data.model.MessageEvent
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.Collections


object SocketHandler {
  private const val SERVER_URL = "http://192.168.0.12:34582" // Replace with your Socket.io server URL

  private var socket: Socket? = null
  private val gson = Gson()

  fun initializeSocket(token: String, context: Context) {
    try {
      val options = IO.Options()
      options.forceNew = true
      options.reconnection = true
      options.auth = Collections.singletonMap("token", token)
      socket = IO.socket(SERVER_URL, options)
      if(socket != null) {
        socket?.open()
        socket?.on(Socket.EVENT_CONNECT) {
          println("Socket connected ${socket?.isActive}")
        }
        socket?.on(Socket.EVENT_DISCONNECT) {
          println("Socket disconnected ${socket?.isActive}")
        }
        socket?.on(Socket.EVENT_CONNECT_ERROR) {
          println("Socket connect error ${socket?.isActive}")
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