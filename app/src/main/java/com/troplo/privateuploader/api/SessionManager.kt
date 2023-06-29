package com.troplo.privateuploader.api

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.troplo.privateuploader.BuildConfig
import com.troplo.privateuploader.R
import com.troplo.privateuploader.data.model.User

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

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

    fun setUserCache(user: User?) {
        val editor = prefs.edit()
        editor.putString("user", Gson().toJson(user))
        editor.apply()
    }

    fun getUserCache(): User? {
        val user = prefs.getString("user", null)
        return if (user != null) {
            Gson().fromJson(user, User::class.java)
        } else {
            null
        }
    }

    fun getInstanceURL(): String {
        return prefs.getString("instanceURL", BuildConfig.SERVER_URL) ?: BuildConfig.SERVER_URL
    }

    fun setInstanceURL(url: String) {
        val editor = prefs.edit()
        editor.putString("instanceURL", url)
        editor.apply()
    }
}