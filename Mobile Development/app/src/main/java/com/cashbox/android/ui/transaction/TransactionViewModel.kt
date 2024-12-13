package com.cashbox.android.ui.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashbox.android.data.model.ExpenseData
import com.cashbox.android.data.model.IncomeData
import com.cashbox.android.data.model.TransactionData
import com.cashbox.android.data.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionViewModel(private val transactionRepository: TransactionRepository): ViewModel() {
    private val _transaction = MutableLiveData<List<TransactionData>>()
    private val _totalBalance = MutableLiveData<Long>()
    private val _totalIncome = MutableLiveData<Long>()
    private val _totalExpense = MutableLiveData<Long>()
    private val _message = MutableLiveData<String>()
    private val _exception = MutableLiveData<Boolean>()

    val transaction: LiveData<List<TransactionData>> = _transaction
    val totalBalance: LiveData<Long> = _totalBalance
    val totalIncome: LiveData<Long> = _totalIncome
    val totalExpense: LiveData<Long> = _totalExpense
    val message: LiveData<String> = _message
    val exception: LiveData<Boolean> = _exception

    fun getAllTransaction(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val allTransactions = transactionRepository.getAllTransaction().data.filter {
                    it.uid == uid
                }
                val totalIncome = allTransactions.filter { it.transactionType == "pemasukan" }
                    .sumOf { it.amount }
                val totalExpense = allTransactions.filter { it.transactionType == "pengeluaran" }
                    .sumOf { it.amount }

                _transaction.postValue(allTransactions)
                _totalBalance.postValue(totalIncome - totalExpense)
                _totalIncome.postValue(totalIncome)
                _totalExpense.postValue(totalExpense)

                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun updateIncomeTransaction(id: Int, incomeData: IncomeData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _message.postValue(
                    transactionRepository.updateIncomeTransactionById(id, incomeData).message
                )
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun updateExpenseTransaction(id: Int, expenseData: ExpenseData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _message.postValue(
                    transactionRepository.updateExpenseTransactionById(id, expenseData).message
                )
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun deleteIncomeTransaction(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _message.postValue(
                    transactionRepository.deleteIncomeTransactionById(id).message
                )
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun deleteExpenseTransaction(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _message.postValue(
                    transactionRepository.deleteExpenseTransactionById(id).message
                )
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun resetExceptionValue() {
        _exception.value = false
    }
}