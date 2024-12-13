package com.cashbox.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cashbox.android.data.repository.UserRepository
import com.cashbox.android.data.repository.WalletRepository
import com.cashbox.android.ui.auth.LoginViewModel
import com.cashbox.android.ui.auth.RegisterViewModel
import com.cashbox.android.ui.wallet.WalletViewModel

@Suppress("UNCHECKED_CAST")
class UserViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}