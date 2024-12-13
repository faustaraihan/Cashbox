package com.cashbox.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cashbox.android.data.repository.TransactionRepository
import com.cashbox.android.ui.analysis.AnalysisViewModel
import com.cashbox.android.ui.transaction.AddTransactionViewModel
import com.cashbox.android.ui.transaction.TransactionViewModel

@Suppress("UNCHECKED_CAST")
class TransactionViewModelFactory(
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TransactionViewModel::class.java) -> {
                TransactionViewModel(transactionRepository) as T
            }
            modelClass.isAssignableFrom(AddTransactionViewModel::class.java) -> {
                AddTransactionViewModel(transactionRepository) as T
            }
            modelClass.isAssignableFrom(AnalysisViewModel::class.java) -> {
                AnalysisViewModel(transactionRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}