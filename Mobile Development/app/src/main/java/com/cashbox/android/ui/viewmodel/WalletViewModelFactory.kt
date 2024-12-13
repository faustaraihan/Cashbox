package com.cashbox.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cashbox.android.data.repository.WalletRepository
import com.cashbox.android.ui.wallet.WalletViewModel

@Suppress("UNCHECKED_CAST")
class WalletViewModelFactory(
    private val walletRepository: WalletRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(WalletViewModel::class.java) -> {
                WalletViewModel(walletRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}