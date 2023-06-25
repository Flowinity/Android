package com.troplo.privateuploader.api

import android.content.Context
import android.content.SharedPreferences
import com.troplo.privateuploader.R

class SessionManager (context: Context) {
  private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

  companion object {
    const val USER_TOKEN = "token"
  }

  fun saveAuthToken(token: String?) {
    val editor = prefs.edit()
    editor.putString(USER_TOKEN, token)
    editor.apply()
  }

  fun getAuthToken(): String? {
    return prefs.getString(USER_TOKEN, null)
  }

  fun getLastChatId(): Int {
    return prefs.getInt("lastChatId", 0)
  }

  fun setFolder(folder: String) {
    val editor = prefs.edit()
    editor.putString("folder", folder)
    editor.apply()
  }

  fun setLastChatId(id: Int) {
    val editor = prefs.edit()
    editor.putInt("lastChatId", id)
    editor.apply()
  }
}