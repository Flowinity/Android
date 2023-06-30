package com.troplo.privateuploader

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.FileObserver
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File

class UploadService : Service() {
    private var fileObserver: FileObserver? = null
    override fun onCreate() {
        super.onCreate()

        // Create a FileObserver to monitor the Screenshots directory
            fileObserver = @RequiresApi(Build.VERSION_CODES.Q)
            object : FileObserver(File(SCREENSHOT_DIRECTORY), CREATE) {
            override fun onEvent(event: Int, path: String?) {
                if (event == CREATE) {
                    Log.d(
                        TAG,
                        "New screenshot created: $path"
                    )
                    // You can perform any actions you need here when a new file is created
                }
            }
        }

        // Start watching for file changes
        fileObserver?.startWatching()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Stop watching for file changes
        fileObserver!!.stopWatching()
    }

    override fun onBind(intent: Intent): IBinder? {
        // This service does not support binding
        return null
    }

    companion object {
        private const val TAG = "ScreenshotWatcher"
        private const val SCREENSHOT_DIRECTORY = "/storage/emulated/0/Pictures/Screenshots"
    }
}