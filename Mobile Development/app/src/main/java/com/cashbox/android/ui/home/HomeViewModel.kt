package com.cashbox.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashbox.android.data.model.BudgetingResponse
import com.cashbox.android.data.model.ListGoals
import com.cashbox.android.data.model.TransactionData
import com.cashbox.android.data.repository.BudgetingRepository
import com.cashbox.android.data.repository.GoalsRepository
import com.cashbox.android.data.repository.TransactionRepository
import com.cashbox.android.data.repository.WalletRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeViewModel(
    private val walletRepository: WalletRepository,
    private val budgetingRepository: BudgetingRepository,
    private val transactionRepository: TransactionRepository,
    private val goalsRepository: GoalsRepository
) : ViewModel() {
    private val _walletTotalAmount = MutableLiveData<Long>()
    private val _topBudgeting = MutableLiveData<List<BudgetingResponse>>()
    private val _lastTransaction = MutableLiveData<List<TransactionData>>()
    private val _topGoals = MutableLiveData<List<ListGoals>>()
    private val _incomeTotalAmount = MutableLiveData<Long>()
    private val _expenseTotalAmount = MutableLiveData<Long>()
    private val _exception = MutableLiveData<Boolean>()

    val walletTotalAmount: LiveData<Long> = _walletTotalAmount
    val topBudgeting: LiveData<List<BudgetingResponse>> = _topBudgeting
    val lastTransaction: LiveData<List<TransactionData>> = _lastTransaction
    val topGoals: LiveData<List<ListGoals>> = _topGoals
    val incomeTotalAmount: LiveData<Long> = _incomeTotalAmount
    val expenseTotalAmount: LiveData<Long> = _expenseTotalAmount
    val exception: LiveData<Boolean> = _exception

    fun getWalletTotalAmount(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _walletTotalAmount.postValue(
                    walletRepository.getWallet().data.filter { it.uid == uid}.sumOf { it.amount }
                )
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun getTopBudgeting(uid: String) {
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
                    }.sortedByDescending { it.urgency }.take(3)

                _topBudgeting.postValue(result)
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun getLastTransaction(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _lastTransaction.postValue(
                    transactionRepository.getAllTransaction().data.filter { it.uid == uid }.take(3)
                )
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun getTopGoals(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
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
                }.take(3)
                _topGoals.postValue(exGoalsList)
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun getIncomeAndExpenseTotalAmount(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val calendar = Calendar.getInstance()
                val currentMonth = calendar.get(Calendar.MONTH)
                val currentYear = calendar.get(Calendar.YEAR)

                val allTransactions = transactionRepository.getAllTransaction().data.filter {
                    it.uid == uid
                }

                val transactionsThisMonth = allTransactions.filter { item ->
                    val date = dateFormat.parse(item.date.substring(0, 10))
                    val itemCalendar = Calendar.getInstance()
                    itemCalendar.time = date!!
                    itemCalendar.get(Calendar.MONTH) == currentMonth &&
                            itemCalendar.get(Calendar.YEAR) == currentYear
                }

                _incomeTotalAmount.postValue(
                    transactionsThisMonth.filter { it.transactionType == "pemasukan" }
                        .sumOf { it.amount }
                )
                _expenseTotalAmount.postValue(
                    transactionsThisMonth.filter { it.transactionType == "pengeluaran" }
                        .sumOf { it.amount }
                )
                _exception.postValue(false)
            } catch (e: Exception) {
                _exception.postValue(true)
            }
        }
    }

    fun resetExceptionValue() {
        _exception.value = false
    }
}