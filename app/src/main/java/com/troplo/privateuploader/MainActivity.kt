package com.troplo.privateuploader

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.SocketHandlerService
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.ui.theme.PrivateUploaderTheme

class MainActivity : ComponentActivity() {

    override fun onStart() {
        super.onStart()
        stopService(Intent(this, ChatService::class.java))
        val socket = SocketHandler.getSocket()
        if (socket != null && !socket.connected()) {
            socket.connect()
        }
    }

    override fun onStop() {
        super.onStop()
        startService(Intent(this, ChatService::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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
        if (token != null) {
            setContent {
                PrivateUploaderTheme {
                    MainScreen()
                }
            }
        } else {
            TpuApi.init("", this)
            setContent {
                PrivateUploaderTheme {
                    MainScreen()
                }
            }
        }
        super.onCreate(savedInstanceState)
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
}

