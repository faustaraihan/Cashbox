package com.cashbox.android.data.repository

import com.cashbox.android.data.api.ApiService
import com.cashbox.android.data.model.TransactionResponse
import com.cashbox.android.data.model.WalletAddBody
import com.cashbox.android.data.model.WalletGetByIdHeader
import com.cashbox.android.data.model.WalletGetHeader
import com.cashbox.android.data.model.WalletPostResponse
import com.cashbox.android.data.model.WalletUpdateBody

class WalletRepository(private val apiService: ApiService) {
    suspend fun getWallet(): WalletGetHeader {
        return apiService.getWallet()
    }

    suspend fun addWallet(walletAddBody: WalletAddBody): WalletPostResponse {
        return apiService.addWallet(walletAddBody)
    }

    suspend fun getWalletById(id: Int): WalletGetByIdHeader {
        return apiService.getWalletById(id)
    }

    suspend fun updateWalletById(id: Int, walletUpdateBody: WalletUpdateBody): WalletPostResponse {
        return apiService.updateWalletById(id, walletUpdateBody)
    }

    suspend fun deleteWallet(id: Int): TransactionResponse {
        return apiService.deleteWallet(id)
    }
}