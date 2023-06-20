package com.troplo.privateuploader.api

import android.content.Context
import android.widget.Toast
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONObject

class RetrofitErrorHandler(private val context: Context) {

  private val httpClient: OkHttpClient

  init {
    // Create an OkHttpClient with the interceptor
    httpClient = OkHttpClient.Builder()
      .addInterceptor(ErrorInterceptor())
      .build()
  }

  fun getHttpClient(): OkHttpClient {
    return httpClient
  }

  inner class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
      val request = chain.request()
      val response = chain.proceed(request)

      if (!response.isSuccessful) {
        // Handle error response
        val errorBody = response.body?.string()
        val errorMessage = extractErrorMessage(errorBody)
        showToast(errorMessage)
        throw Throwable(errorMessage)
      }

      return response
    }
  }

  fun extractErrorMessage(errorBody: String?): String {
    // Parse the error body and extract the error message
    // Adjust this logic based on the response structure of your API
    errorBody?.let {
      // Assuming the error response is in JSON format
      val errorJson = JSONObject(it)
      val errorsArray = errorJson.getJSONArray("errors")
      if (errorsArray.length() > 0) {
        val firstError = errorsArray.getJSONObject(0)
        return firstError.getString("message")
      }
    }

    return "Unknown Error"
  }

  private fun showToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
  }
}