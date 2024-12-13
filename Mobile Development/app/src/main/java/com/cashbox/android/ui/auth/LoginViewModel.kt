package com.cashbox.android.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashbox.android.data.model.AuthError
import com.cashbox.android.data.model.LoginBody
import com.cashbox.android.data.model.LoginGoogleBody
import com.cashbox.android.data.model.LoginResponse
import com.cashbox.android.data.repository.UserRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse>()
    private val _loginGoogleResponse = MutableLiveData<LoginResponse>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _errorMessage = MutableLiveData<String>()
    private val _exception = MutableLiveData<Boolean>()

    val loginResponse: LiveData<LoginResponse> = _loginResponse
    val loginGoogleResponse: LiveData<LoginResponse> = _loginGoogleResponse
    val isLoading: LiveData<Boolean> = _isLoading
    val errorMessage: LiveData<String> = _errorMessage
    val exception: LiveData<Boolean> = _exception

    fun userLoginWithGoogle(loginGoogleBody: LoginGoogleBody) {
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userRepository.userLoginWithGoogle(loginGoogleBody)

                if (response.isSuccessful) {
                    _loginGoogleResponse.postValue(response.body())
                } else {
                    val errorJson = response.errorBody()?.string()
                    val apiError = Gson().fromJson(errorJson, AuthError::class.java)
                    _errorMessage.postValue(apiError.message)
                }
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun userLogin(loginBody: LoginBody) {
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userRepository.userLogin(loginBody)

                if (response.isSuccessful) {
                    _loginResponse.postValue(response.body())
                } else {
                    val errorJson = response.errorBody()?.string()
                    val apiError = Gson().fromJson(errorJson, AuthError::class.java)
                    _errorMessage.postValue(apiError.message)
                }
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun resetExceptionValue() {
        _exception.value = false
    }
}