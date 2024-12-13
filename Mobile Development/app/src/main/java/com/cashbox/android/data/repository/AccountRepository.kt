package com.cashbox.android.data.repository

import com.cashbox.android.data.api.ApiService
import com.cashbox.android.data.model.AccountBody
import com.cashbox.android.data.model.AccountHeader
import com.cashbox.android.data.model.TransactionResponse

class AccountRepository(private val apiService: ApiService) {
    suspend fun getUserData(uid: String): AccountHeader {
        return apiService.getUserData(uid)
    }

    suspend fun updateUserData(accountBody: AccountBody): TransactionResponse {
        return apiService.updateUserData(accountBody)
    }
}