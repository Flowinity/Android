package com.troplo.privateuploader.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.SocketHandlerService
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.core.LoadingButton
import com.troplo.privateuploader.data.model.LoginRequest
import com.troplo.privateuploader.ui.theme.Primary
import com.troplo.privateuploader.ui.theme.PrivateUploaderTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val viewModel = remember { LoginViewModel() }

    Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TPU",
            style = MaterialTheme.typography.displayMedium,
            color = Primary
        )
        Text(
            text = "Mobile Beta",
            style = MaterialTheme.typography.bodyLarge,
            color = Primary
        )
        Spacer(modifier = Modifier.height(32.dp))
        val instanceState = remember { mutableStateOf(SessionManager(context).getInstanceURL()) }
        LaunchedEffect(instanceState.value) {
            delay(500)
            viewModel.checkInstance(instanceState.value, context)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            TextField(
                value = instanceState.value,
                onValueChange = { instanceState.value = it },
                label = { Text("PrivateUploader Instance") },
                supportingText = { Text(viewModel.instanceVersion.value) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(32.dp))
            val usernameState = remember { mutableStateOf("") }
            TextField(
                value = usernameState.value,
                onValueChange = { usernameState.value = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            val passwordState = remember { mutableStateOf("") }
            TextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            val totpState = remember { mutableStateOf("") }
            TextField(
                value = totpState.value,
                onValueChange = { totpState.value = it },
                label = { Text("2FA code (if enabled)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            LoadingButton(
                onClick = {
                    viewModel.login(
                        username = usernameState.value,
                        password = passwordState.value,
                        totp = totpState.value,
                        context = context,
                        onLoginSuccess = onLoginSuccess
                    )
                },
                loading = viewModel.loading,
                text = "Login",
                enabled = usernameState.value.isNotEmpty() && passwordState.value.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    PrivateUploaderTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            LoginScreen(
                onLoginSuccess = {

                }
            )
        }
    }
}

class LoginViewModel : ViewModel() {
    var loading by mutableStateOf(false)
    var instanceVersion = mutableStateOf("Loading...")

    fun login(
        username: String,
        password: String,
        totp: String,
        context: Context,
        onLoginSuccess: () -> Unit,
    ) {
        loading = true
        SessionManager(context).saveAuthToken(null)
        viewModelScope.launch(Dispatchers.IO) {
            val data = TpuApi.retrofitService.login(
                LoginRequest(
                    username,
                    password,
                    totp
                )
            ).execute()

            launch(Dispatchers.Main) {
                loading = false
                if (data.isSuccessful) {
                    // set the token
                    UserStore.logout(context)
                    val token = data.body()!!.token
                    SessionManager(context).saveAuthToken(token)
                    // go to the main screen
                    TpuApi.init(data.body()!!.token, context)
                    SocketHandler.closeSocket()
                    SocketHandler.initializeSocket(token, context)
                    UserStore.initializeUser(context)
                    onLoginSuccess()
                }
            }
        }
    }

    fun checkInstance(instance: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            TpuApi.instance = instance
            val data = TpuApi.retrofitService.getInstanceInfo().execute()
            launch(Dispatchers.Main) {
                if (data.isSuccessful) {
                    val body = data.body()!!
                    instanceVersion.value = "${body.name} - Connected"
                    TpuApi.instance = instance
                    SocketHandler.baseUrl = instance
                    SocketHandlerService.baseUrl = instance
                    SessionManager(context).setInstanceURL(instance)
                } else {
                    instanceVersion.value = "Error connecting to instance"
                }
            }
        }
    }
}