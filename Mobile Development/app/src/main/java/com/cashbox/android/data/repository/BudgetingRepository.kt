package com.cashbox.android.data.repository

import com.cashbox.android.data.api.ApiService
import com.cashbox.android.data.model.BudgetingBody
import com.cashbox.android.data.model.BudgetingHeader
import com.cashbox.android.data.model.TransactionResponse

class BudgetingRepository(private val apiService: ApiService) {
    suspend fun addBudgeting(budgetingBody: BudgetingBody) {
        apiService.addBudgeting(budgetingBody)
    }

    suspend fun getBudgeting(): BudgetingHeader {
        return apiService.getBudgeting()
    }

    suspend fun deleteBudgeting(id: Int): TransactionResponse {
        return apiService.deleteBudgeting(id)
    }
}