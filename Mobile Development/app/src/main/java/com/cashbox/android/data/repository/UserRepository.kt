package com.cashbox.android.data.repository

import com.cashbox.android.data.api.ApiService
import com.cashbox.android.data.model.LoginBody
import com.cashbox.android.data.model.LoginGoogleBody
import com.cashbox.android.data.model.LoginResponse
import com.cashbox.android.data.model.RegisterBody
import com.cashbox.android.data.model.RegisterResponse
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {
    suspend fun userLoginWithGoogle(loginGoogleBody: LoginGoogleBody): Response<LoginResponse> {
        return apiService.userLoginWithGoogle(loginGoogleBody)
    }

    suspend fun userLogin(loginBody: LoginBody): Response<LoginResponse> {
        return apiService.userLogin(loginBody)
    }

    suspend fun userRegister(registerBody: RegisterBody): Response<RegisterResponse> {
        return apiService.userRegister(registerBody)
    }
}