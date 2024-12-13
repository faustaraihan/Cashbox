package com.cashbox.android.data.model

import com.google.gson.annotations.SerializedName

data class WalletAddBody(
    val uid: String,
    @SerializedName("nama")
    val name: String,
    @SerializedName("nominal")
    val amount: Long
)

data class WalletUpdateBody(
    @SerializedName("nama")
    val name: String
)

data class WalletPostResponse(
    val message: String
)

data class WalletGetHeader(
    val data: List<WalletData>
)

data class WalletGetByIdHeader(
    val data: WalletData
)

data class WalletData(
    val id: Int,
    @SerializedName("nama")
    val name: String,
    @SerializedName("nominal")
    val amount: Long,
    val uid: String
)
