package com.cashbox.android.ui.budgeting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashbox.android.data.model.BudgetingBody
import com.cashbox.android.data.model.BudgetingResponse
import com.cashbox.android.data.repository.BudgetingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BudgetingViewModel(private val budgetingRepository: BudgetingRepository) : ViewModel() {
    private val _budgetingData = MutableLiveData<List<BudgetingResponse>>()
    private val _selectedCategory = MutableLiveData<String>()
    private val _responseSuccess = MutableLiveData<Boolean>()
    private val _exception = MutableLiveData<Boolean>()

    val budgetingData: LiveData<List<BudgetingResponse>> = _budgetingData
    val selectedCategory: LiveData<String> = _selectedCategory
    val responseSuccess: LiveData<Boolean> = _responseSuccess
    val exception: LiveData<Boolean> = _exception

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun getBudgeting(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = budgetingRepository.getBudgeting().data
                    .filter { it.uid == uid }
                    .fold(mutableListOf<BudgetingResponse>()) { result, current ->
                        val existing = result.find { it.category == current.category }

                        if (existing != null) {
                            existing.amount += current.amount
                            existing.urgency += current.urgency

                            if (!existing.ids.contains(current.id)) {
                                existing.ids.add(current.id)
                            }
                        } else {
                            result.add(
                                BudgetingResponse(
                                    id = current.id,
                                    uid = current.uid,
                                    category = current.category,
                                    amount = current.amount,
                                    urgency = current.urgency,
                                    ids = mutableListOf(current.id)
                                )
                            )
                        }
                        result
                    }.sortedByDescending { it.urgency }

                _budgetingData.postValue(result)
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun addBudgeting(budgetingBody: BudgetingBody) {
        viewModelScope.launch {
            try {
                budgetingRepository.addBudgeting(budgetingBody)
                _exception.postValue(false)
                _responseSuccess.postValue(true)
            } catch (e: Exception) {
                _exception.postValue(true)
                _responseSuccess.postValue(false)
            }
        }
    }

    fun deleteBudgeting(id: Int) {
        viewModelScope.launch {
            try {
                budgetingRepository.deleteBudgeting(id).message
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun resetSelectedCategory() {
        _selectedCategory.value = ""
    }

    fun resetResponseValue() {
        _responseSuccess.value = false
    }

    fun resetExceptionValue() {
        _exception.value = false
    }
}