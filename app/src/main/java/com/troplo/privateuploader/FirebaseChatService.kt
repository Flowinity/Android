package com.troplo.privateuploader

import android.Manifest
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.data.model.FCMTokenRequest
import com.troplo.privateuploader.data.model.MessageEventFirebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FirebaseChatService : FirebaseMessagingService() {
    private val messages = mutableMapOf<Int, MutableList<NotificationCompat.MessagingStyle.Message>>()

    private fun isAppOnForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = context.packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == packageName) {
                return true
            }
        }
        return false
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "[NewChatService] Message received")
        if(isAppOnForeground(this)) {
            Log.d(TAG, "[NewChatService] App is on foreground")
            return
        }
        sendNotification(
            MessageEventFirebase(
                content = remoteMessage.data["content"] ?: "",
                userId = remoteMessage.data["userId"]?.toInt() ?: 0,
                username = remoteMessage.data["username"] ?: "",
                createdAt = remoteMessage.data["createdAt"] ?: "",
                chatName = remoteMessage.data["chatName"] ?: "",
                associationId = remoteMessage.data["associationId"]?.toInt() ?: 0,
                avatar = remoteMessage.data["avatar"] ?: "",
                id = remoteMessage.data["id"]?.toInt() ?: 0
            )
        )
    }
    // [END receive_message]

    private fun needsToBeScheduled() = true

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    private fun scheduleJob() {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .build()
        WorkManager.getInstance(this)
            .beginWith(work)
            .enqueue()
        // [END dispatch_job]
    }

    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    private fun sendRegistrationToServer(token: String?) {
        if(token != null) {
            Log.d("$TAG.FCMToken", token)
            CoroutineScope(Dispatchers.IO).launch {
                TpuApi.retrofitService.registerFcmToken(FCMTokenRequest(token))
            }
        }
    }

    private fun sendNotification(message: MessageEventFirebase) {
        Log.d("TPU.Untagged", "[ChatService] Sending notification")

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
        asyncLoadIcon(message.avatar, this) {
            try {
                Log.d("TPU.Untagged", "[ChatService] Loaded icon")
                val chatPartner = Person.Builder().apply {
                    setName(message.username)
                    setKey(message.userId.toString())
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
                if (messages[message.associationId] == null) messages[message.associationId] = mutableListOf()
                messages[message.associationId]?.add(
                    NotificationCompat.MessagingStyle.Message(
                        message.content,
                        TpuFunctions.getDate(message.createdAt)?.time ?: 0,
                        chatPartner
                    )
                )

                val style = NotificationCompat.MessagingStyle(chatPartner)
                    .setConversationTitle(message.chatName)

                for (msg in messages[message.associationId]!!) {
                    style.addMessage(msg)
                }

                val replyIntent = Intent(this, InlineNotificationActivity::class.java)
                replyIntent.putExtra("chatId", 69)
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
                    .setContentTitle(message.username)
                    .setSmallIcon(R.drawable.tpu_logo)
                    .setWhen(TpuFunctions.getDate(message.createdAt)?.time ?: 0)
                    .addAction(replyAction)
                val res = notificationManager.notify(message.associationId, builder.build())
                Log.d("TPU.Untagged", "[ChatService] Notification sent, $res")
            } catch (e: Exception) {
                Log.d("TPU.Untagged", "[ChatService] Error sending notification, ${e.printStackTrace()}")
            }
        }
    }

    companion object {
        private const val TAG = "FirebaseChatService"
    }

    internal class MyWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            // TODO(developer): add long running task here.
            return Result.success()
        }
    }
}