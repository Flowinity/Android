package com.troplo.privateuploader.api.stores

import android.content.Context
import androidx.navigation.NavController
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.Friend
import com.troplo.privateuploader.data.model.MessageEvent
import com.troplo.privateuploader.data.model.StatusPayload
import com.troplo.privateuploader.data.model.User
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException


object AppStore {
    var navController: NavController? = null
}