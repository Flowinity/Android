package com.troplo.privateuploader

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.data.model.MessageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable


class InlineNotificationActivity: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("TPU.Untagged", "[ChatService] InlineNotificationActivity onCreate, intent: $intent, extras: ${intent.extras}")

        val chatId = intent.getIntExtra("chatId", 0)
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        val content = remoteInput?.getCharSequence("content")?.toString()
        TpuApi.init(SessionManager(context).getAuthToken() ?: "", context)
        sendReply(chatId, content, context)
    }

    private fun sendReply(chatId: Int, content: String?, context: Context) {
        if(chatId == 0) return
        Log.d("TPU.Untagged", "Sending reply to chatId: $chatId")
        CoroutineScope(Dispatchers.IO).launch {
            val response = TpuApi.retrofitService.sendMessage(id = chatId, messageRequest = MessageRequest(
                content = content ?: ""
            )).execute()
        }
    }
}