package com.troplo.privateuploader.api

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Source
import okio.buffer
import java.io.IOException

private class ProgressResponseBody(
    private val responseBody: ResponseBody?,
    private val callback: ProgressCallback
) : ResponseBody() {
    private var bufferedSource: BufferedSource? = null

    override fun contentType(): MediaType? = responseBody?.contentType()

    override fun contentLength(): Long = responseBody?.contentLength() ?: -1

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody?.source()).buffer()
        }
        return bufferedSource as BufferedSource
    }

    private fun source(source: Source?): Source {
        return object : ForwardingSource(source!!) {
            private var totalBytesRead: Long = 0

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                val progress = (totalBytesRead * 100f) / (responseBody?.contentLength() ?: 0)
                callback.onProgressUpdate(progress)
                return bytesRead
            }
        }
    }
}


interface ProgressCallback {
    fun onProgressUpdate(progress: Float)
}