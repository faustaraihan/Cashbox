package com.cashbox.android.data.model

import com.google.gson.annotations.SerializedName

data class GoalsListHeader(
    val data: List<GoalsData>
)

data class GoalsSingleHeader(
    val data: GoalsData
)

data class GoalsData(
    val id: Int,
    val uid: String,
    @SerializedName("nama")
    val name: String,
    @SerializedName("nominal")
    val amount: Long,
    @SerializedName("tgl_tercapai")
    val targetDate: String,
)

data class ListGoals(
    val idGoals: Int,
    val name: String,
    val currentAmount: Long,
    val targetAmount: Long
)

data class GoalsBody(
    val uid: String,
    @SerializedName("nama")
    val name: String,
    @SerializedName("nominal")
    val amount: Long,
    @SerializedName("tgl_tercapai")
    val targetDate: String,
)

data class SaveHeader(
    val data: List<SaveData>
)

data class SaveData(
    val id: Int,
    val uid: String,
    @SerializedName("goal_id")
    val goalId: Int,
    @SerializedName("deskripsi")
    val description: String,
    @SerializedName("nominal")
    val amount: Long,
    @SerializedName("tgl_tabung")
    val date: String
)

data class SaveBody(
    val uid: String,
    @SerializedName("goal_id")
    val goalId: Int,
    @SerializedName("deskripsi")
    val description: String,
    @SerializedName("nominal")
    val amount: Long,
    @SerializedName("tgl_tabung")
    val date: String
)
