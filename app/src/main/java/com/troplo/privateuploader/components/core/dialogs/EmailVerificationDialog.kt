package com.troplo.privateuploader.components.core.dialogs

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.core.InteractionDialog
import com.troplo.privateuploader.components.core.LoadingButton
import com.troplo.privateuploader.ui.theme.Primary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun EmailVerificationDialog(open: MutableState<Boolean> = mutableStateOf(true), navigate: (String) -> Unit = {}) {
    val viewModel = EmailVerificationViewModel()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.resend(context)
    }
    InteractionDialog(
        header = {
            TopAppBar(
                title = {
                    Text("Confirm your email address")
                }
            )
        },
        button = {
            Column {
                LoadingButton(
                    onClick = {
                        viewModel.resend(context)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Resend/recheck verification",
                    loading = viewModel.loading.value
                )
                ClickableText(text = AnnotatedString("Logout"), onClick = {
                    UserStore.logout(context)
                    navigate("login")
                },
            modifier = Modifier
                        .align(Alignment.CenterHorizontally).padding(top = 16.dp, bottom = 16.dp),
                style = MaterialTheme.typography.bodyMedium.copy(color = Primary)
                )
            }
        },
        open = open,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Back",
                    modifier = Modifier
                        .padding(16.dp)
                        .size(64.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "Please check your email for a verification link. If you don't see it, check your spam folder. It may take up to 5 minutes to arrive.",
                    modifier = Modifier
                        .padding(16.dp)
                )
            }
        }
    )
}

class EmailVerificationViewModel: ViewModel() {
    val loading = mutableStateOf(false)
    fun resend(context: Context) {
        loading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val response = TpuApi.retrofitService.sendVerificationEmail().execute()
            if(response.code() == 404) {
                // User is assumed to be verified, recheck APIs
                UserStore.initializeUser(context)
                val token = SessionManager(context).getAuthToken()
                SocketHandler.initializeSocket(token ?: "", context)
            }
            loading.value = false
        }
    }
}