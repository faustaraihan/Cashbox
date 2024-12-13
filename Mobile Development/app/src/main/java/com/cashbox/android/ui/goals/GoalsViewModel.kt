package com.cashbox.android.ui.goals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashbox.android.data.model.GoalsData
import com.cashbox.android.data.model.ListGoals
import com.cashbox.android.data.model.SaveData
import com.cashbox.android.data.repository.GoalsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class GoalsViewModel(private val goalsRepository: GoalsRepository): ViewModel() {
    private val _goals = MutableLiveData<List<GoalsData>>()
    private val _saves = MutableLiveData<List<SaveData>>()
    private val _goal = MutableLiveData<GoalsData>()
    private val _savesByGoal = MutableLiveData<List<SaveData>>()
    private val _differences = MutableLiveData<Pair<Long, Long>>()
    private val _listGoals = MutableLiveData<List<ListGoals>>()
    private val _deleteMessage = MutableLiveData<String>()
    private val _exception = MutableLiveData<Boolean>()

    val goals: LiveData<List<GoalsData>> = _goals
    val saves: LiveData<List<SaveData>> = _saves
    val goal: LiveData<GoalsData> = _goal
    val savesByGoal: LiveData<List<SaveData>> = _savesByGoal
    val differences: LiveData<Pair<Long, Long>> = _differences
    val listGoals: LiveData<List<ListGoals>> = _listGoals
    val deleteMessage: LiveData<String> = _deleteMessage
    val exception: LiveData<Boolean> = _exception

    fun getListGoals(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _saves.postValue(goalsRepository.getAllSaves().data.filter { it.uid == uid })

                val goals = goalsRepository.getAllGoals().data.filter { it.uid == uid }
                val saves = goalsRepository.getAllSaves().data.filter { it.uid == uid }

                val groupedSaves = saves.groupBy { it.goalId }

                val exGoalsList = goals.map { goal ->
                    val targetAmount = goal.amount
                    val currentAmount = groupedSaves[goal.id]?.sumOf { it.amount } ?: 0L
                    ListGoals(
                        idGoals = goal.id,
                        name = goal.name,
                        targetAmount = targetAmount,
                        currentAmount = currentAmount
                    )
                }
                _listGoals.postValue(exGoalsList)
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun getGoalsDetail(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _goal.postValue(goalsRepository.getGoalsDetail(id).data)
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun getSavesByGoal(id: Int, uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _savesByGoal.postValue(
                    goalsRepository.getSavesByGoal(id, uid).data.sortedByDescending {
                        LocalDate.parse(it.date.substring(0, 10))
                    }
                )
                _differences.postValue(Pair(
                    goalsRepository.getGoalsDetail(id).data.amount,
                    goalsRepository.getSavesByGoal(id, uid).data.sumOf { it.amount }
                ))
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun deleteGoals(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _deleteMessage.postValue(goalsRepository.deleteGoals(id).message)
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun resetDeleteMessageValue() {
        _deleteMessage.value = ""
    }

    fun resetExceptionValue() {
        _exception.value = false
    }
}