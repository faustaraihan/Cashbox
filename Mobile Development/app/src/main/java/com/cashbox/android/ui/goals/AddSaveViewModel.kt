package com.cashbox.android.ui.goals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashbox.android.data.model.SaveBody
import com.cashbox.android.data.repository.GoalsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddSaveViewModel(private val goalsRepository: GoalsRepository): ViewModel() {
    private val _addSaveResponse = MutableLiveData<String>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _exception = MutableLiveData<Boolean>()

    val addSaveResponse: LiveData<String> = _addSaveResponse
    val isLoading: LiveData<Boolean> = _isLoading
    val exception: LiveData<Boolean> = _exception

    fun addSave(saveBody: SaveBody) {
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _addSaveResponse.postValue(goalsRepository.addSave(saveBody).message)
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun editSave(id: Int, saveBody: SaveBody) {
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _addSaveResponse.postValue(goalsRepository.editSave(id, saveBody).message)
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun deleteSave(id: Int, uid: String) {
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _addSaveResponse.postValue(goalsRepository.deleteSave(id, uid).message)
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