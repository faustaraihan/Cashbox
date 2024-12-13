package com.cashbox.android.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashbox.android.data.model.WalletAddBody
import com.cashbox.android.data.model.WalletData
import com.cashbox.android.data.model.WalletUpdateBody
import com.cashbox.android.data.repository.WalletRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WalletViewModel(private val walletRepository: WalletRepository): ViewModel() {
    private val _wallet = MutableLiveData<List<WalletData>>()
    private val _walletDetail = MutableLiveData<WalletData>()
    private val _messageAddSuccess = MutableLiveData<String>()
    private val _messageUpdateSuccess = MutableLiveData<String>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _exception = MutableLiveData<Boolean>()

    val wallet: LiveData<List<WalletData>> = _wallet
    val walletDetail: LiveData<WalletData> = _walletDetail
    val messageAddSuccess: LiveData<String> = _messageAddSuccess
    val messageUpdateSuccess: LiveData<String> = _messageUpdateSuccess
    val isLoading: LiveData<Boolean> = _isLoading
    val exception: LiveData<Boolean> = _exception

    fun getWallet(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _wallet.postValue(walletRepository.getWallet().data.filter { it.uid == uid })
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun addWallet(walletAddBody: WalletAddBody) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _messageAddSuccess.postValue(walletRepository.addWallet(walletAddBody).message)
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getWalletById(id: Int) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _walletDetail.postValue(walletRepository.getWalletById(id).data)
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun updateWalletById(id: Int, walletUpdateBody: WalletUpdateBody) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val message = walletRepository.updateWalletById(id, walletUpdateBody).message
                _messageUpdateSuccess.postValue(message)
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun deleteWallet(id: Int) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val message = walletRepository.deleteWallet(id).message
                _messageUpdateSuccess.postValue(message)
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

    fun resetMessageUpdateSuccessValue() {
        _messageUpdateSuccess.value = ""
    }
}