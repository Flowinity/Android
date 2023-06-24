package com.troplo.privateuploader.api

import android.content.Context
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import java.net.URI
import java.net.URISyntaxException
import java.util.Collections


object SocketHandler {
  private const val SERVER_URL = "http://192.168.0.12:34582" // Replace with your Socket.io server URL

  private var socket: Socket? = null

  fun initializeSocket(token: String) {
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
        // socket on any other events
        socket?.on("message") {
          println("Socket $it")
        }
        println("Socket connected ${socket?.isActive}")
      } else {
        println("Socket is null")
      }
    } catch (e: URISyntaxException) {
      e.printStackTrace()
    }
  }

  fun getSocket(): Socket? {
    return socket
  }

  fun closeSocket() {
    socket?.disconnect()
    socket = null
  }
}