package com.troplo.privateuploader.api

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import kotlinx.coroutines.Dispatchers

fun imageLoader(context: Context, cache: Boolean? = true): ImageLoader {
    return ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .dispatcher(Dispatchers.IO)
        .diskCache {
            if (cache == true) {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(20 * 1024 * 1024)
                    .build()
            } else {
                null
            }
        }
        .respectCacheHeaders(false)
        .build()
}