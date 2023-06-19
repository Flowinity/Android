package com.troplo.privateuploader.api

import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.troplo.privateuploader.R
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.net.URISyntaxException
import java.util.Collections.singletonMap


object SocketHandler {
  lateinit var tpuSocket: Socket

  @Synchronized
  fun setSocket(token: String?) {
    try {
      val options = IO.Options.builder()
        .setAuth(singletonMap("token", token))
        .build()
      tpuSocket = IO.socket("http://192.168.0.12:34582", options)
      println("Socket set")
    } catch (e: URISyntaxException) {
      e.printStackTrace()
    }
  }

  @Synchronized
  fun getSocket(): Socket {
    return tpuSocket
  }

  @Synchronized
  fun establishConnection() {
    tpuSocket.connect()
  }

  @Synchronized
  fun closeConnection() {
    tpuSocket.disconnect()
  }

  @Synchronized
  fun listeners() {
    tpuSocket.on("message", Emitter.Listener { args->
      /*NotificationCompat.Builder(this, "communications")
        .setStyle(NotificationCompat.MessagingStyle("Me")
          .setConversationTitle(TpuFunctions.getChatName(args[0].chat as Chat?))
          .build()
        )*/
      println(args)
    })

    tpuSocket.on(Socket.EVENT_CONNECT) {
      println("Connected to TPU Server")
    }
    tpuSocket.on(Socket.EVENT_DISCONNECT) {
      println("Disconnected from TPU Server")
    }
    tpuSocket.on("echo") { args ->
      println(args[0])
    }
  }
}