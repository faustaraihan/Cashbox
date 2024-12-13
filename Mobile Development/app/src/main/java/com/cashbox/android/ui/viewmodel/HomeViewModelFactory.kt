package com.cashbox.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cashbox.android.data.repository.BudgetingRepository
import com.cashbox.android.data.repository.GoalsRepository
import com.cashbox.android.data.repository.TransactionRepository
import com.cashbox.android.data.repository.WalletRepository
import com.cashbox.android.ui.home.HomeViewModel

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(
    private val walletRepository: WalletRepository,
    private val budgetingRepository: BudgetingRepository,
    private val transactionRepository: TransactionRepository,
    private val goalsRepository: GoalsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    walletRepository,
                    budgetingRepository,
                    transactionRepository,
                    goalsRepository
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}