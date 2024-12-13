package com.cashbox.android.data.model

import com.google.gson.annotations.SerializedName

data class BudgetingEntry(
    val category: String,
    val amount: Long
)

data class BudgetingBody(
    val uid: String,
    @SerializedName("kategori")
    val category: Int,
    @SerializedName("nominal")
    val amount: Long,
    @SerializedName("urgensi")
    val urgency: Float
)

data class BudgetingHeader(
    val data: List<BudgetingResponse>
)

data class BudgetingResponse(
    val id: Int,
    val uid: String,
    @SerializedName("kategori")
    val category: Int,
    @SerializedName("nominal")
    var amount: Long,
    @SerializedName("urgensi")
    var urgency: Float,
    val ids: MutableList<Int> = mutableListOf()
)