package com.troplo.privateuploader

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.common.GoogleApiAvailability
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.SocketHandlerService
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.ui.theme.PrivateUploaderTheme

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
}

