package com.cashbox.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cashbox.android.data.repository.BudgetingRepository
import com.cashbox.android.ui.budgeting.BudgetingViewModel

@Suppress("UNCHECKED_CAST")
class BudgetingViewModelFactory(
    private val budgetingRepository: BudgetingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(BudgetingViewModel::class.java) -> {
                BudgetingViewModel(budgetingRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}