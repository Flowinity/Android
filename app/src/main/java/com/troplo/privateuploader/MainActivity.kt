package com.troplo.privateuploader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.User
import com.troplo.privateuploader.ui.theme.PrivateUploaderTheme

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
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

