package com.troplo.privateuploader.data

import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.LoginRequest
import com.troplo.privateuploader.data.model.LoginResponse
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String, code: String?): Result<LoginResponse> {
        try {
            val login = TpuApi.retrofitService.login(
                LoginRequest(
                    username,
                    password,
                    code ?: ""
                )
            ).execute().body()

            return if (login != null) {
                Result.Success(LoginResponse(login.token, login.user))
            } else {
                Result.Error(IOException("Error logging in"))
            }
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}