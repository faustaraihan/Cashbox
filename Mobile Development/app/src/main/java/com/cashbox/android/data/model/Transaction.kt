package com.cashbox.android.data.model

import com.google.gson.annotations.SerializedName

data class TransactionHeader(
    val data: List<TransactionData>
)

data class TransactionData(
    val uid: String,
    @SerializedName("id_pemasukan")
    val transactionId: Int,
    @SerializedName("deskripsi")
    val description: String,
    @SerializedName("nominal")
    val amount: Long,
    @SerializedName("sumber_uang")
    val source: Int,
    @SerializedName("tanggal")
    val date: String,
    @SerializedName("kategori_masuk")
    val category: Int,
    @SerializedName("tipe")
    val transactionType: String,
    @SerializedName("nama_sumber_uang")
    val sourceName: String
)

data class IncomeBody(
    val uid: String,
    @SerializedName("deskripsi")
    val description: String,
    @SerializedName("nominal")
    val amount: Long,
    @SerializedName("fk_sumber_uang")
    val source: Int,
    @SerializedName("tanggal")
    val date: String,
    @SerializedName("kategori_masuk")
    val category: Int,
    @SerializedName("nama_sumber_uang")
    val sourceName: String
)

data class ExpenseBody(
    val uid: String,
    @SerializedName("deskripsi")
    val description: String,
    @SerializedName("nominal")
    val amount: Long,
    @SerializedName("sumber_uang")
    val source: Int,
    @SerializedName("tanggal")
    val date: String,
    @SerializedName("kategori_keluar")
    val category: Int,
    @SerializedName("nama_sumber_uang")
    val sourceName: String
)

data class IncomeHeader(
    val data: IncomeData
)

data class IncomeData(
    @SerializedName("deskripsi")
    val description: String,
    @SerializedName("nominal")
    val amount: Long,
    @SerializedName("kategori_masuk")
    val category: Int,
    @SerializedName("tanggal")
    val date: String,
    @SerializedName("fk_sumber_uang")
    val source: Int,
    @SerializedName("nama_sumber_uang")
    val sourceName: String
)

data class ExpenseData(
    @SerializedName("deskripsi")
    val description: String,
    @SerializedName("nominal")
    val amount: Long,
    @SerializedName("kategori_keluar")
    val category: Int,
    @SerializedName("tanggal")
    val date: String,
    @SerializedName("sumber_uang")
    val source: Int
)

data class TransactionResponse(
    val message: String
)