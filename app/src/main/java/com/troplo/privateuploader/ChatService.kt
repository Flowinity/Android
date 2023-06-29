// NO LONGER USED, REPLACED BY FirebaseChatService.kt

package com.troplo.privateuploader

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import coil.request.ImageRequest
import coil.size.Size
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.SocketHandlerService
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.imageLoader
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.data.model.Message
import com.troplo.privateuploader.data.model.MessageEvent
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import java.net.URISyntaxException


class ChatService : Service() {
    private var socket: Socket? = SocketHandlerService.getSocket()
    private val messages = mutableMapOf<Int, MutableList<NotificationCompat.MessagingStyle.Message>>()

    override fun onCreate() {
        super.onCreate()
        try {
            Log.d("TPU.Untagged", "[ChatService] Started")
            if(socket == null || !socket!!.connected()) {
                val token = SessionManager(this).getAuthToken()
                if(!token.isNullOrBlank()) {
                    SocketHandlerService.initializeSocket(token, this, "android_kotlin_background_service")
                    socket = SocketHandlerService.getSocket()
                }
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        socket?.on("message", onNewMessage)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle incoming messages from Socket.io
        socket?.connect()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TPU.Untagged", "[ChatService] Stopped")
        socket?.disconnect()
        socket?.off("message", onNewMessage)
    }

    private val onNewMessage: Emitter.Listener = object : Emitter.Listener {
        override fun call(vararg args: Any?) {
            Log.d("TPU.Untagged", "[ChatService] Message received")

            // Process the new message
            val jsonArray = args[0] as JSONObject
            val payload = jsonArray.toString()
            val messageEvent = SocketHandler.gson.fromJson(payload, MessageEvent::class.java)

            val message = messageEvent.message

            // Send a notification using the Conversations API
            sendNotification(message)
        }
    }

    private fun sendNotification(message: Message?) {
        Log.d("TPU.Untagged", "[ChatService] Sending notification, ${message == null || message.userId == UserStore.getUser()?.id}")
        if(message == null) return

        // Add any additional configuration to the notification builder as needed
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("TPU.Untagged", "[ChatService] No permission to post notifications")
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        asyncLoadIcon(message.user?.avatar, this) {
            try {
                Log.d("TPU.Untagged", "[ChatService] Loaded icon")
                val chatPartner = Person.Builder().apply {
                    setName(message.user?.username)
                    setKey(message.user?.id.toString())
                    setIcon(it)
                    setImportant(false)
                }.build()

                val notificationManager = NotificationManagerCompat.from(this)
                val channel = NotificationChannel(
                    "communications",
                    "Messages from Communications",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
                if (messages[message.chatId] == null) messages[message.chatId] = mutableListOf()
                messages[message.chatId]?.add(
                    NotificationCompat.MessagingStyle.Message(
                        message.content,
                        TpuFunctions.getDate(message.createdAt)?.time ?: 0,
                        chatPartner
                    )
                )

                val style = NotificationCompat.MessagingStyle(chatPartner)
                    .setConversationTitle("Conversation in ${message.chatId}")

                for (msg in messages[message.chatId]!!) {
                    style.addMessage(msg)
                }

                val replyIntent = Intent(this, InlineNotificationActivity::class.java)
                replyIntent.putExtra("chatId", message.chatId)
                val replyPendingIntent = PendingIntent.getBroadcast(this, 0, replyIntent, PendingIntent.FLAG_MUTABLE)

                val remoteInput = RemoteInput.Builder("content")
                    .setLabel("Reply")
                    .build()

                val replyAction = NotificationCompat.Action.Builder(
                    R.drawable.tpu_logo,
                    "Reply",
                    replyPendingIntent
                )
                    .addRemoteInput(remoteInput)
                    .setAllowGeneratedReplies(true)
                    .build()

                val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "communications")
                    .addPerson(chatPartner)
                    .setStyle(style)
                    .setContentText(message.content)
                    .setContentTitle(message.user?.username)
                    .setSmallIcon(R.drawable.tpu_logo)
                    .setWhen(TpuFunctions.getDate(message.createdAt)?.time ?: 0)
                    .addAction(replyAction)
                val res = notificationManager.notify(message.chatId, builder.build())
                Log.d("TPU.Untagged", "[ChatService] Notification sent, $res")
            } catch (e: Exception) {
                Log.d("TPU.Untagged", "[ChatService] Error sending notification, ${e.printStackTrace()}")
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

fun asyncLoadIcon(avatar: String?, context: Context, setIcon: (IconCompat?) -> Unit) {
    if (avatar.isNullOrEmpty())
        setIcon(null)
    else {
        val request = ImageRequest.Builder(context)
            .dispatcher(Dispatchers.IO)
            .data(data = TpuFunctions.image(avatar, null))
            .apply {
                size(Size.ORIGINAL)
            }
            .target { drawable ->
                try {
                    val bitmap = (drawable as BitmapDrawable).bitmap
                    val roundedBitmap = createRoundedBitmap(bitmap)

                    val roundedIcon = IconCompat.createWithBitmap(roundedBitmap)

                    setIcon(roundedIcon)
                } catch (e: Exception) {
                    Log.d("TPU.Untagged", e.toString())
                    setIcon(null)
                }
            }
            .build()
        imageLoader(context).enqueue(request)
    }
}

private fun createRoundedBitmap(bitmap: Bitmap): Bitmap {
    return try {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        canvas.drawRoundRect(rect, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)

        output
    } catch (e: Exception) {
        bitmap
    }
}