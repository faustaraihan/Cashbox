package com.cashbox.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cashbox.android.data.repository.GoalsRepository
import com.cashbox.android.ui.goals.AddGoalsViewModel
import com.cashbox.android.ui.goals.AddSaveViewModel
import com.cashbox.android.ui.goals.GoalsViewModel

@Suppress("UNCHECKED_CAST")
class GoalsViewModelFactory(
    private val goalsRepository: GoalsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GoalsViewModel::class.java) -> {
                GoalsViewModel(goalsRepository) as T
            }
            modelClass.isAssignableFrom(AddSaveViewModel::class.java) -> {
                AddSaveViewModel(goalsRepository) as T
            }
            modelClass.isAssignableFrom(AddGoalsViewModel::class.java) -> {
                AddGoalsViewModel(goalsRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}