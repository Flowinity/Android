package com.troplo.privateuploader.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.troplo.privateuploader.data.LoginRepository
import com.troplo.privateuploader.data.Result

import com.troplo.privateuploader.R

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

  private val _loginForm = MutableLiveData<LoginFormState>()
  val loginFormState: LiveData<LoginFormState> = _loginForm

  private val _loginResult = MutableLiveData<LoginResult>()
  val loginResult: LiveData<LoginResult> = _loginResult

  fun login(username: String, password: String, code: String?) {
    // can be launched in a separate asynchronous job
    val result = loginRepository.login(username, password, code)

    if (result is Result.Success) {
      _loginResult.value =
        LoginResult(success = LoggedInUserView(displayName = result.data.user.username))
    } else {
      _loginResult.value = LoginResult(error = R.string.login_failed)
    }
  }
}