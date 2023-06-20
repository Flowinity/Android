package com.troplo.privateuploader.ui.login

import android.app.Activity
import android.content.Context
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.troplo.privateuploader.databinding.ActivityLoginBinding

import com.troplo.privateuploader.R
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.LoginRequest
import com.troplo.privateuploader.data.model.LoginResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

  private lateinit var loginViewModel: LoginViewModel
  private lateinit var binding: ActivityLoginBinding

  val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    println("CoroutineExceptionHandler got $throwable")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val username = binding.username
    val password = binding.password
    val login = binding.login
    val loading = binding.loading
    val code = binding.code
    val token = binding.token

    loginViewModel = ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]

    login.setOnClickListener {
      try {
        println(binding.token.text.toString())
        if(binding.token.text.toString() != "") {
          SessionManager(this).saveAuthToken(binding.token.text.toString())
          setResult(Activity.RESULT_OK)
          finish()
        }
        println("login" + username.text.toString() + password.text.toString() + code.text.toString())
        loading.visibility = View.VISIBLE
        login(username, password, code, token, loading, this)
        loading.visibility = View.GONE
        setResult(Activity.RESULT_OK)
        finish()
      } catch (e: Exception) {
        println(e.toString())
        loading.visibility = View.GONE
        Toast.makeText(this, "Login error.", Toast.LENGTH_LONG).show()
      }
    }
  }

  private fun login(username: EditText, password: EditText, code: EditText, token: EditText, loading: View, context: Context) {
    val scope = CoroutineScope(Dispatchers.Main + exceptionHandler) // Use Dispatchers.Main to update UI

    val loginDeferred = scope.async {
      try {
        val user = withContext(Dispatchers.IO) {
          TpuApi.retrofitService.login(
            LoginRequest(
              username.text.toString(),
              password.text.toString(),
              code.text.toString()
            )
          ).execute().body()
        }

        if (user != null) {
          SessionManager(context).saveAuthToken(user.token)
          setResult(Activity.RESULT_OK)
          finish()
        } else {
          throw Exception("Login error.")
        }
      } catch (e: Exception) {
        println(e.toString())
        throw e // Rethrow the exception to propagate it to the calling function
      }
    }

    scope.launch {
      try {
        loginDeferred.await() // Await the completion of the loginDeferred
      } catch (e: Exception) {
        println(e.toString())
        // Handle the exception here in the original calling function
        loading.visibility = View.GONE
        Toast.makeText(context, "Login error.", Toast.LENGTH_LONG).show()
      }
    }
  }

  private fun showLoginFailed(@StringRes errorString: Int) {
    Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
  }

  /**
   * Extension function to simplify setting an afterTextChanged action to EditText components.
   */
  fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(editable: Editable?) {
        afterTextChanged.invoke(editable.toString())
      }

      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
  }
}