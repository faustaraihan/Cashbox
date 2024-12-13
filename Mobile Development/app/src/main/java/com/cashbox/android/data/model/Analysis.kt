package com.cashbox.android.data.model

import com.google.gson.annotations.SerializedName

data class AnalysisHeader(
    val data: List<AnalysisData>
)

data class AnalysisData(
    val uid: String,
    @SerializedName("kategori")
    val category: Int,
    @SerializedName("nominal")
    val amount: Long,
    @SerializedName("tipe")
    val type: String
)
