package com.cashbox.android.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashbox.android.data.model.AccountBody
import com.cashbox.android.data.model.AccountData
import com.cashbox.android.data.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountViewModel(private val accountRepository: AccountRepository) : ViewModel() {
    private val _userData = MutableLiveData<AccountData>()
    private val _message = MutableLiveData<String>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _exception = MutableLiveData<Boolean>()

    val userData: LiveData<AccountData> = _userData
    val message: LiveData<String> = _message
    val isLoading: LiveData<Boolean> = _isLoading
    val exception: LiveData<Boolean> = _exception

    fun getUserData(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _userData.postValue(accountRepository.getUserData(uid).data)
                _exception.postValue(false)
            } catch(e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun updateUserData(accountBody: AccountBody) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _message.postValue(accountRepository.updateUserData(accountBody).message)
                _exception.postValue(false)
            } catch(e: Exception) {
                _exception.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun resetMessageValue() {
        _message.value = ""
    }

    fun resetExceptionValue() {
        _exception.value = false
    }
}