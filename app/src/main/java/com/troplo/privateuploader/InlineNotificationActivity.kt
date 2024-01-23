package com.troplo.privateuploader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.RemoteInput
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.MessageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class InlineNotificationActivity : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            Log.d(
                "TPU.Untagged",
                "[Firebase] InlineNotificationActivity onCreate, intent: $intent, extras: ${intent.extras}"
            )

            val chatId = intent.getIntExtra("chatId", 0)
            val remoteInput = RemoteInput.getResultsFromIntent(intent)
            val content = remoteInput?.getCharSequence("content")?.toString()
            Log.d("InlineNotificationAct", "Firebase - chatId: $chatId, content: $content")
            TpuApi.init(SessionManager(context).getAuthToken() ?: "", context)
            sendReply(chatId, content, context)
        } catch (e: Exception) {
            Log.d("TPU.InlineNotificationAct", "Firebase - Exception: $e")
        }
    }

    private fun sendReply(chatId: Int, content: String?, context: Context) {
        try {
            if (chatId == 0) return
            Log.d("TPU.Untagged", "Firebase - Sending reply to chatId: $chatId")
            CoroutineScope(Dispatchers.IO).launch {
                val response = TpuApi.retrofitService.sendMessage(
                    id = chatId, messageRequest = MessageRequest(
                        content = content ?: ""
                    )
                ).execute()
            }
        } catch (e: Exception) {
            Log.d("TPU.InlineNotificationAct", "Firebase - sendReply exception: $e")
        }
    }
}