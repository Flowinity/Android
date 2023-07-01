package com.troplo.privateuploader.api

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference


class RequestBodyWithProgress(
    private val files: List<File>,
    private val contentType: ContentType,
    private val progressCallback:((progress: Float)->Unit)?
) : RequestBody() {

    override fun contentType(): MediaType? = contentType.description.toMediaTypeOrNull()

    override fun contentLength(): Long = files.sumOf { it.length() }

    override fun writeTo(sink: BufferedSink) {
        val handler = Handler(Looper.getMainLooper())
        files.forEach { file ->
            val fileLength = file.length()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val inSt = FileInputStream(file)
            var uploaded = 0L
            inSt.use {
                var read: Int = inSt.read(buffer)
                while (read != -1) {
                    progressCallback?.let {
                        uploaded += read
                        val progress = (uploaded.toDouble() / fileLength.toDouble()).toFloat()
                        handler.post { it(progress) }
                    }

                    sink.write(buffer, 0, read)
                    read = inSt.read(buffer)
                }
            }
        }
    }

    enum class ContentType(val description: String) {
        PNG_IMAGE("image/png"),
        JPG_IMAGE("image/jpg"),
        IMAGE("image/*"),
        ANY("*/*")
    }
}