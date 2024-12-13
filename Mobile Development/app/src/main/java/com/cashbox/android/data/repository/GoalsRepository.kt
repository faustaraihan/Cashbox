package com.cashbox.android.data.repository

import com.cashbox.android.data.api.ApiService
import com.cashbox.android.data.model.GoalsBody
import com.cashbox.android.data.model.GoalsListHeader
import com.cashbox.android.data.model.GoalsSingleHeader
import com.cashbox.android.data.model.SaveBody
import com.cashbox.android.data.model.SaveHeader
import com.cashbox.android.data.model.TransactionResponse

class GoalsRepository(private val apiService: ApiService) {
    suspend fun getAllGoals(): GoalsListHeader {
        return apiService.getAllGoals()
    }

    suspend fun addGoals(goalsBody: GoalsBody): TransactionResponse {
        return apiService.addGoals(goalsBody)
    }

    suspend fun editGoals(id: Int, goalsBody: GoalsBody): TransactionResponse {
        return apiService.editGoals(id, goalsBody)
    }

    suspend fun deleteGoals(id: Int): TransactionResponse {
        return apiService.deleteGoals(id)
    }

    suspend fun getAllSaves(): SaveHeader {
        return apiService.getAllSaves()
    }

    suspend fun getGoalsDetail(id: Int): GoalsSingleHeader {
        return apiService.getGoalsDetail(id)
    }

    suspend fun getSavesByGoal(id: Int, uid: String): SaveHeader {
        return apiService.getSaveByGoals(id, uid)
    }

    suspend fun addSave(saveBody: SaveBody): TransactionResponse {
        return apiService.addSave(saveBody)
    }

    suspend fun editSave(id: Int, saveBody: SaveBody): TransactionResponse {
        return apiService.editSave(id, saveBody)
    }

    suspend fun deleteSave(id: Int, uid: String): TransactionResponse {
        return apiService.deleteSave(id, uid)
    }
}