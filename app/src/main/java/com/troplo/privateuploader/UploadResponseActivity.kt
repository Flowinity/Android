package com.troplo.privateuploader

import android.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.troplo.privateuploader.api.stores.UploadStore


class UploadResponseActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UploadStore.intentCode && resultCode == RESULT_OK) {
            Log.d("TPU.UploadResponse", "Upload response received")
            if (data != null) {
                if (data.clipData != null) {
                    // Multiple files were selected
                    val clipData = data.clipData
                    if (clipData != null) {
                        for (i in 0 until clipData.itemCount) {
                            val uri = clipData.getItemAt(i).uri
                            Log.d("TPU.UploadResponse", uri.toString())
                        }
                    }
                } else if (data.data != null) {
                    // Single file was selected
                    val uri = data.data
                    Log.d("TPU.UploadResponse", uri.toString())
                }
            }
        }
    }
}

