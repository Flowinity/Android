package com.troplo.privateuploader.api.stores

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.ActivityCompat.startActivityForResult
import com.troplo.privateuploader.MainActivity
import com.troplo.privateuploader.UploadResponseActivity
import com.troplo.privateuploader.data.model.UploadTarget
import java.util.Date


object UploadStore {
    var intentCode = 0
    var filePath: String = ""
    var uploads = mutableStateListOf<UploadTarget>()
    var globalProgress: MutableState<Float> = mutableFloatStateOf(0f)

    fun requestUploadIntent(activity: Activity) {
        intentCode = Date().time.toInt()
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            type = "*/*"
        }
        startActivityForResult(activity, intent, intentCode, null)
    }
}