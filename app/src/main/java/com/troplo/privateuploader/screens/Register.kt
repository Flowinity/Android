package com.troplo.privateuploader.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
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
import com.troplo.privateuploader.data.model.RegisterRequest
import com.troplo.privateuploader.ui.theme.Primary
import com.troplo.privateuploader.ui.theme.PrivateUploaderTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onLoginSuccess: () -> Unit, navigate: (String) -> Unit) {
    val context = LocalContext.current
    val viewModel = remember { RegisterViewModel() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PrivateUploader",
            style = MaterialTheme.typography.displayMedium,
            color = Primary,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
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
            val emailState = remember { mutableStateOf("") }
            TextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text("Email") },
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
            LoadingButton(
                onClick = {
                    viewModel.register(
                        username = usernameState.value,
                        password = passwordState.value,
                        email = emailState.value,
                        context = context,
                        onLoginSuccess = onLoginSuccess
                    )
                },
                loading = viewModel.loading,
                text = "Register",
                enabled = usernameState.value.isNotEmpty() && passwordState.value.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                ClickableText(
                    text = AnnotatedString("Already have an account?"),
                    style = TextStyle(
                        color = Primary,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                    ),
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = {
                        navigate("login")
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    PrivateUploaderTheme(
        content = {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                RegisterScreen(
                    onLoginSuccess = {},
                    navigate = {}
                )
            }
        }
    )
}

class RegisterViewModel : ViewModel() {
    var loading by mutableStateOf(false)
    var instanceVersion = mutableStateOf("Loading...")

    fun register(
        username: String,
        password: String,
        email: String,
        context: Context,
        onLoginSuccess: () -> Unit,
    ) {
        loading = true
        SessionManager(context).saveAuthToken(null)
        viewModelScope.launch(Dispatchers.IO) {
            val data = TpuApi.retrofitService.register(
                RegisterRequest(
                    username,
                    password,
                    email
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
                    Toast.makeText(context, "Welcome to PrivateUploader!", Toast.LENGTH_SHORT).show()
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