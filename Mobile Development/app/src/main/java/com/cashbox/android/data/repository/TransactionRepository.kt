package com.cashbox.android.data.repository

import com.cashbox.android.data.api.ApiService
import com.cashbox.android.data.model.AnalysisHeader
import com.cashbox.android.data.model.ExpenseBody
import com.cashbox.android.data.model.ExpenseData
import com.cashbox.android.data.model.IncomeBody
import com.cashbox.android.data.model.IncomeData
import com.cashbox.android.data.model.IncomeHeader
import com.cashbox.android.data.model.TransactionHeader
import com.cashbox.android.data.model.TransactionResponse
import com.cashbox.android.data.model.WalletGetHeader

class TransactionRepository(private val apiService: ApiService) {
    suspend fun getAllTransaction(): TransactionHeader {
        return apiService.getAllTransaction()
    }

    suspend fun getAllWallet(): WalletGetHeader {
        return apiService.getWallet()
    }

    suspend fun addIncomeTransaction(incomeBody: IncomeBody): TransactionResponse {
        return apiService.addIncomeTransaction(incomeBody)
    }

    suspend fun addExpenseTransaction(expenseBody: ExpenseBody): TransactionResponse {
        return apiService.addExpenseTransaction(expenseBody)
    }

    suspend fun getTransactionOnSpecificMonth(month: Int, year: Int): AnalysisHeader {
        return apiService.getTransactionOnSpecificMonth(month, year)
    }

    suspend fun updateIncomeTransactionById(id: Int, incomeData: IncomeData): TransactionResponse {
        return apiService.updateIncomeTransactionById(id, incomeData)
    }

    suspend fun deleteIncomeTransactionById(id: Int): TransactionResponse {
        return apiService.deleteIncomeTransaction(id)
    }

    suspend fun updateExpenseTransactionById(id: Int, expenseData: ExpenseData): TransactionResponse {
        return apiService.updateExpenseTransactionById(id, expenseData)
    }

    suspend fun deleteExpenseTransactionById(id: Int): TransactionResponse {
        return apiService.deleteExpenseTransaction(id)
    }
}