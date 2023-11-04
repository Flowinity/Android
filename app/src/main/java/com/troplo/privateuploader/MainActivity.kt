package com.troplo.privateuploader

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.core.content.ContextCompat
import androidx.startup.AppInitializer
import com.google.android.gms.common.GoogleApiAvailability
import com.troplo.privateuploader.api.RequestBodyWithProgress
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.SocketHandlerService
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.stores.CollectionStore
import com.troplo.privateuploader.api.stores.CoreStore
import com.troplo.privateuploader.api.stores.UploadStore
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.data.model.UploadTarget
import com.troplo.privateuploader.ui.theme.PrivateUploaderTheme
import io.wax911.emojify.EmojiManager
import io.wax911.emojify.initializer.EmojiInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody


class MainActivity : ComponentActivity() {
    override fun onResume() {
        super.onResume()
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
        val socket = SocketHandler.getSocket()
        if (socket != null && !socket.connected()) {
            socket.connect()
        }
    }

    override fun onStart() {
        super.onStart()
        val socket = SocketHandler.getSocket()
        if (socket != null && !socket.connected()) {
            socket.connect()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
        startService(Intent(this, UploadService::class.java))
        // if(BuildConfig.DEBUG) StrictMode.enableDefaults();
        Log.d("MainActivity.Instance", SessionManager(this).getInstanceURL())
        TpuApi.instance = SessionManager(this).getInstanceURL()
        SocketHandler.baseUrl = SessionManager(this).getInstanceURL()
        SocketHandlerService.baseUrl = SessionManager(this).getInstanceURL()
        val token = SessionManager(this).getAuthToken()
        TpuApi.init(token ?: "", this)
        if (token != null) {
            SocketHandler.initializeSocket(token, this)
            UserStore.initializeUser(this)
        }
        CoreStore.initializeCore()
        if (token != null) {
            setContent {
                PrivateUploaderTheme(
                    selected = SessionManager(this).theme,
                    content = {
                        MainScreen()
                    }
                )
            }
        } else {
            TpuApi.init("", this)
            setContent {
                PrivateUploaderTheme(
                    selected = SessionManager(this).theme,
                    content = {
                        MainScreen()
                    }
                )
            }
        }
        super.onCreate(savedInstanceState)
        askNotificationPermission()
        /*
                fun requestPermissions() {
                    val permissions = arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA
                    )
                    ActivityCompat.requestPermissions(this, permissions, 0)
                }

                val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        // There are no request codes
                        val data: Intent? = result.data
                        val folder = data?.getStringExtra("folder")
                        if(folder != null) {
                            SessionManager(this).setFolder(folder)
                        }
                    }
                }
                fun requestFolder() {
                    requestPermissions()
                    val intent = Intent(this, MainActivity::class.java)
                    resultLauncher.launch(intent)
                }
                requestFolder()*/
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    public fun upload(files: List<UploadTarget>, deleteOnceFinished: Boolean = true, context: Context = this) {
        Log.d("TPU.Upload", "Uploading ${files.size} files")
        if(deleteOnceFinished) {
            UploadStore.uploads = files.toMutableStateList()
        }

        val filesBody = files.map { file ->
            TpuFunctions.uriToFile(file.uri, context, file.name)
        }

        CoroutineScope(Dispatchers.IO).launch {
            var totalSize = 0L

            // Calculate total file size
            filesBody.forEach { file ->
                totalSize += file.length()
            }

            val parts = mutableListOf<MultipartBody.Part>()

            val requestFile = RequestBodyWithProgress(
                filesBody,
                RequestBodyWithProgress.ContentType.ANY,
                progressCallback = { progress ->
                    Log.d("TPU.Upload", "Progress: $progress")
                    UploadStore.globalProgress.value = progress
                }
            )

            filesBody.forEach { file ->
                val part = MultipartBody.Part.createFormData(
                    "attachments",
                    file.name,
                    requestFile
                )
                parts.add(part)
            }

            val response = TpuApi.retrofitService.uploadFiles(parts).execute()
            response.body()?.let { upload ->
                UploadStore.globalProgress.value = 0f
                if(deleteOnceFinished) {
                    UploadStore.uploads.clear()
                } else {
                    // set the upload status to finished
                    UploadStore.uploads.find { it.uri == files.first().uri }?.let {
                        it.progress = 100f
                        it.url = upload[0].upload.attachment
                    }
                    Log.d("TPU.Upload", "Upload finished: ${upload[0].upload.attachment}, ${UploadStore.uploads.toList().toString()}")
                }
            }
        }
    }

    @Deprecated("Deprecated in Java :(")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TPU.Upload", "Upload response received (MainActivity)")
        if (requestCode == UploadStore.intentCode && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.clipData != null) {
                    // Multiple files were selected
                    val clipData = data.clipData
                    if (clipData != null) {
                        val files = mutableListOf<UploadTarget>()
                        for (i in 0 until clipData.itemCount) {
                            Log.d("TPU.Upload", "File: ${clipData.getItemAt(i).uri}")
                            val uri = clipData.getItemAt(i).uri
                            files.add(UploadTarget(
                                uri = uri,
                                name = getFileName(uri) ?: "unknown.file"
                            ))
                        }
                        upload(files)
                    }
                } else if (data.data != null) {
                    val uri = data.data
                    if (uri != null) {
                        upload(listOf(UploadTarget(
                            uri = uri,
                            name = getFileName(uri) ?: "unknown.file"
                        )))
                    }
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor.use { c ->
                if (c != null && c.moveToFirst()) {
                    val index = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    result = c.getString(index)
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result as String
    }


    internal val emojiManager: EmojiManager by lazy {
        // should already be initialized if we haven't disabled initialization in manifest
        // see: https://developer.android.com/topic/libraries/app-startup#disable-individual
        AppInitializer.getInstance(this)
            .initializeComponent(EmojiInitializer::class.java)
    }
}

