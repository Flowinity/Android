package com.troplo.privateuploader.api

import android.content.SharedPreferences

class TpuConfig (private val sharedPref: SharedPreferences) {
  var token: String?
    get() = sharedPref.getString("token", null)
    set(value) = sharedPref.edit().putString("token", value).apply()

  var instance: String? = "https://privateuploader.com"
}