package com.cashbox.android.data.model

import com.google.gson.annotations.SerializedName

data class AccountHeader(
    val data: AccountData
)

data class AccountData(
    val name: String,
    val email: String,
    @SerializedName("tgl_lahir")
    val birthDate: String,
    @SerializedName("no_telp")
    val number: String,
    val gender: String
)

data class AccountBody(
    val uid: String,
    @SerializedName("tgl_lahir")
    val birthDate: String,
    @SerializedName("no_telp")
    val number: String,
    val gender: String
)