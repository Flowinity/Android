package com.troplo.privateuploader.api

import android.text.format.DateFormat
import com.troplo.privateuploader.data.model.Chat
import com.troplo.privateuploader.data.model.User
import java.util.Date
import kotlin.math.ln
import kotlin.math.pow

object TpuFunctions {
    fun image(link: String?, recipient: User?): String? {
        if (recipient?.avatar != null) {
            return "https://i.troplo.com/i/${recipient.avatar}"
        }
        if (link == null) {
            return null
        }
        return if (link.length >= 20) {
            "https://colubrina.troplo.com/usercontent/$link"
        } else {
            "https://i.troplo.com/i/$link"
        }
    }

    fun getChatName(chat: Chat?): String {
        if (chat == null) {
            return "Communications"
        }
        return if (chat.type == "direct") {
            chat.recipient?.username ?: "Deleted User"
        } else {
            chat.name
        }
    }

    fun formatDate(date: Date?): CharSequence? {
        if (DateFormat.format("dd MMMM yyyy", date) == DateFormat.format("dd MMMM yyyy", Date())) {
            return DateFormat.format("hh:mm:ss a", date)
        }
        return DateFormat.format("dd MMMM yyyy, hh:mm:ss a", date)
    }

    fun fileSize(size: Int?): String {
        if (size == null || size == 0) return "0B"
        val unit = 1024
        if (size < unit) return "$size B"
        val exp = (Math.log(size.toDouble()) / ln(unit.toDouble())).toInt()
        val pre = "KMGTPE"[exp - 1] + "i"
        return String.format("%.1f %sB", size / unit.toDouble().pow(exp.toDouble()), pre)
    }
}