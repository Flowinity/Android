package com.troplo.privateuploader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.User
import com.troplo.privateuploader.ui.theme.PrivateUploaderTheme
import io.socket.client.IO
import kotlinx.coroutines.Dispatchers
import java.util.Collections

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val token = SessionManager(this).fetchAuthToken()
        var user: User? = null
        if(token != null) {
            TpuApi.retrofitService.getUser(token).enqueue(object : retrofit2.Callback<User> {
                override fun onResponse(
                    call: retrofit2.Call<User>,
                    response: retrofit2.Response<User>
                ) {
                    if (response.body()?.username != null) {
                        println("User is logged in")
                        user = response.body()!!
                    } else {
                        println("User is not logged in")
                    }
                    setContent {
                        PrivateUploaderTheme {
                            MainScreen(user)
                        }
                    }
                }

                override fun onFailure(call: retrofit2.Call<User>, t: Throwable) {
                    println("User is not logged in")
                    setContent {
                        PrivateUploaderTheme {
                            MainScreen(user)
                        }
                    }
                }
            })
        } else {
            setContent {
                PrivateUploaderTheme {
                    MainScreen(user)
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

