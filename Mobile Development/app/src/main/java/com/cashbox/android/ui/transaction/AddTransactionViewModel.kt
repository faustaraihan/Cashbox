package com.cashbox.android.ui.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashbox.android.R
import com.cashbox.android.data.model.ExpenseBody
import com.cashbox.android.data.model.IncomeBody
import com.cashbox.android.data.model.WalletData
import com.cashbox.android.data.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTransactionViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val _transactionType = MutableLiveData<String>().apply { value = "Pemasukan" }
    private val _transactionCategory = MutableLiveData<String>()
    private val _transactionSource = MutableLiveData<String>()
    private val _wallet = MutableLiveData<List<WalletData>>()
    private val _responseMessage = MutableLiveData<String>()
    private val _incomeButtonBackground = MutableLiveData<Int>()
    private val _expenseButtonBackground = MutableLiveData<Int>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _exception = MutableLiveData<Boolean>()

    val transactionType: LiveData<String> = _transactionType
    val transactionCategory: LiveData<String> = _transactionCategory
    val transactionSource: LiveData<String> = _transactionSource
    val wallet: LiveData<List<WalletData>> = _wallet
    val responseMessage: LiveData<String> = _responseMessage
    val incomeButtonBackground: LiveData<Int> = _incomeButtonBackground
    val expenseButtonBackground: LiveData<Int> = _expenseButtonBackground
    val isLoading: LiveData<Boolean> = _isLoading
    val exception: LiveData<Boolean> = _exception

    fun changeTransactionType(transactionType: String) {
        _transactionType.value = transactionType
        updateButtonBackground(transactionType)
    }

    fun setTransactionCategory(transactionCategory: String) {
        _transactionCategory.value = transactionCategory
    }

    fun setTransactionSource(transactionSource: String) {
        _transactionSource.value = transactionSource
    }

    private fun updateButtonBackground(transactionType: String) {
        val isIncome = transactionType == "Pemasukan"
        _incomeButtonBackground.value = if (isIncome) R.drawable.bg_btn_light_blue else
            R.drawable.bg_btn_white
        _expenseButtonBackground.value = if (isIncome) R.drawable.bg_btn_white else
            R.drawable.bg_btn_light_blue
    }

    fun getAllWallet(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _wallet.postValue(transactionRepository.getAllWallet().data.filter { it.uid == uid })
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun addIncomeTransaction(incomeBody: IncomeBody) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _responseMessage.postValue(
                    transactionRepository.addIncomeTransaction(incomeBody).message
                )
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun addExpenseTransaction(expenseBody: ExpenseBody) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _responseMessage.postValue(
                    transactionRepository.addExpenseTransaction(expenseBody).message
                )
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun resetResponseMessageValue() {
        _responseMessage.value = ""
    }
}