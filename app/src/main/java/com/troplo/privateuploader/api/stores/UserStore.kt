package com.troplo.privateuploader.api.stores

import android.content.Context
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URISyntaxException


object UserStore {
  private var user: User? = null

  fun initializeUser(context: Context) {
    try {
      CoroutineScope(
        Dispatchers.IO
      ).launch {
        if(SessionManager(context).getUserCache() != null) {
          user = SessionManager(context).getUserCache()
        }
        user = TpuApi.retrofitService.getUser().execute().body()
        SessionManager(context).setUserCache(user)
      }
    } catch (e: URISyntaxException) {
      e.printStackTrace()
    }
  }

  fun getUser(): User? {
    return user
  }

  fun resetUser() {
    user = null
  }
}