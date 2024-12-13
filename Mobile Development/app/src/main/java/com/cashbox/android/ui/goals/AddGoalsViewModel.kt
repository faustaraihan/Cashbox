package com.cashbox.android.ui.goals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashbox.android.data.model.GoalsBody
import com.cashbox.android.data.repository.GoalsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddGoalsViewModel(private val goalsRepository: GoalsRepository): ViewModel() {
    private val _addGoalsResponse = MutableLiveData<String>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _exception = MutableLiveData<Boolean>()

    val addGoalsResponse: LiveData<String> = _addGoalsResponse
    val isLoading: LiveData<Boolean> = _isLoading
    val exception: LiveData<Boolean> = _exception

    fun addGoals(goalsBody: GoalsBody) {
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _addGoalsResponse.postValue(goalsRepository.addGoals(goalsBody).message)
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun editGoals(id: Int, goalsBody: GoalsBody) {
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _addGoalsResponse.postValue(goalsRepository.editGoals(id, goalsBody).message)
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun resetExceptionValue() {
        _exception.value = false
    }
}