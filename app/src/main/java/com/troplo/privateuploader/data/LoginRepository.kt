package com.troplo.privateuploader.data

import com.troplo.privateuploader.data.model.LoginResponse
import com.troplo.privateuploader.data.model.User


/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

  // in-memory cache of the loggedInUser object
  var user: User? = null
    private set

  val isLoggedIn: Boolean
    get() = user != null

  init {
    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    user = null
  }

  fun logout() {
    user = null
    dataSource.logout()
  }

  fun login(username: String, password: String, code: String?): Result<LoginResponse> {
    // handle login
    val result = dataSource.login(username, password, code)

    if (result is Result.Success) {
      setLoggedInUser(result.data.user)
    }

    return result
  }

  private fun setLoggedInUser(loggedInUser: User) {
    this.user = loggedInUser
    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
  }
}