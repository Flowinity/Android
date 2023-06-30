package com.troplo.privateuploader.api

import android.content.Context
import android.net.Uri
import android.util.Log
import com.troplo.privateuploader.data.model.Chat
import com.troplo.privateuploader.data.model.User
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
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


    fun formatDate(date: String?): CharSequence {
        try {
            val utcDateTime = ZonedDateTime.parse(date)
            val localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault())
            val currentDate = LocalDate.now()

            val formatter = if (localDateTime.toLocalDate() == currentDate) {
                DateTimeFormatter.ofPattern("hh:mm:ss a")
            } else {
                DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a")
            }

            return localDateTime.format(formatter)
        } catch (e: Exception) {
            Log.d("TPU.Untagged", "Error formatting date (FD): $e")
            return "Check logcat"
        }
    }

    fun formatDateDay(date: String?): CharSequence {
        try {
            val utcDateTime = ZonedDateTime.parse(date)
            val localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault())

            val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")

            return localDateTime.format(formatter)
        } catch (e: Exception) {
            Log.d("TPU.Untagged", "Error formatting date (FDD): $e")
            return "Check logcat"
        }
    }

    fun currentISODate(): String {
        return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
    }

    fun fileSize(size: Int?): String {
        if (size == null || size == 0) return "0B"
        val unit = 1024
        if (size < unit) return "$size B"
        val exp = (Math.log(size.toDouble()) / ln(unit.toDouble())).toInt()
        val pre = "KMGTPE"[exp - 1] + "i"
        return String.format("%.1f %sB", size / unit.toDouble().pow(exp.toDouble()), pre)
    }

    fun getStatusDetails(status: String): Pair<Long, String> {
        return when (status) {
            "online" -> Pair(0xFF4CAF50, "Online")
            "idle" -> Pair(0xFFFF9800, "Idle")
            "busy" -> Pair(0xFFF44336, "Do Not Disturb")
            "invisible" -> Pair(0xFF757575, "Invisible")
            else -> Pair(0xFF757575, "Offline")
        }
    }

    fun getDate(date: String?): Date? {
        return try {
            val df1: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            df1.parse(date)
        } catch (e: Exception) {
            Log.d("TPU.Untagged", "Error formatting date (GD): $e")
            null
        }
    }

    fun uriToFile(uri: Uri, context: Context, filename: String): File {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(
            uri,
            "r",
            null
        )
        val file = File(
            context.cacheDir,
            filename
        )
        val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        parcelFileDescriptor?.close()
        return file
    }
}