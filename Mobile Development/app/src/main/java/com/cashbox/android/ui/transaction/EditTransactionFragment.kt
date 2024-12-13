package com.cashbox.android.ui.transaction

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.cashbox.android.R
import com.cashbox.android.data.api.ApiClientBearer
import com.cashbox.android.data.datastore.DataStoreInstance
import com.cashbox.android.data.datastore.UserPreference
import com.cashbox.android.data.model.ExpenseData
import com.cashbox.android.data.model.IncomeData
import com.cashbox.android.data.repository.TransactionRepository
import com.cashbox.android.databinding.FragmentEditTransactionBinding
import com.cashbox.android.ui.main.MainActivity
import com.cashbox.android.ui.viewmodel.TransactionViewModelFactory
import com.cashbox.android.utils.AnimationHelper
import com.cashbox.android.utils.DataHelper
import com.cashbox.android.utils.DateHelper
import com.cashbox.android.utils.DateHelper.convertDateToIndonesianFormat
import com.cashbox.android.utils.NumberFormatHelper
import com.cashbox.android.utils.getNumberId
import com.cashbox.android.utils.toExpenseCategoryText
import com.cashbox.android.utils.toIncomeCategoryText
import com.cashbox.android.utils.toIndonesianNumberString
import com.cashbox.android.utils.toOriginalNumber
import kotlinx.coroutines.launch

class EditTransactionFragment : Fragment(R.layout.fragment_edit_transaction) {
    private val binding by viewBinding(FragmentEditTransactionBinding::bind)
    private lateinit var transactionViewModel: TransactionViewModel
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupBackPressedDispatcher()
        setupEditText()
    }

    private fun setupButtons() {
        binding.ibClose.setOnClickListener {
            findNavController().popBackStack()
            (activity as MainActivity).showBottomNav()
        }

        AnimationHelper.applyTouchAnimation(binding.btnSave)
        binding.btnSave.setOnClickListener {
            val description = binding.edtDescription.text.toString()
            val amount = binding.edtAmount.text.toString()
            val category = binding.edtCategory.text.toString()
            val date = DateHelper.convertDateToOriginalValue(binding.edtDate.text.toString())

            if (listOf(description, amount, category, date).any { it.isEmpty() }) {
                showToast(resources.getString(R.string.data_can_not_be_empty))
            } else {
                if (DataHelper.transactionType == "pemasukan") {
                    setupDataStoreIncome(
                        IncomeData(
                            description,
                            amount.toOriginalNumber(),
                            category.getNumberId(),
                            date,
                            DataHelper.transactionSource,
                            DataHelper.transactionSourceName
                        )
                    )
                } else {
                    setupDataStoreExpense(
                        ExpenseData(
                            description,
                            amount.toOriginalNumber(),
                            category.getNumberId(),
                            date,
                            DataHelper.transactionSource
                        )
                    )
                }
            }
        }

        AnimationHelper.applyTouchAnimation(binding.btnDelete)
        binding.btnDelete.setOnClickListener {
            if (DataHelper.transactionType == "pemasukan") {
                setupDataStoreIncomeDelete(DataHelper.transactionId)
            } else {
                setupDataStoreExpenseDelete(DataHelper.transactionId)
            }
        }
    }

    private fun setupBackPressedDispatcher() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                    (activity as MainActivity).showBottomNav()
                }
            }
        )
    }

    private fun setupEditText() {
        binding.edtDescription.setText(DataHelper.transactionDescription)
        binding.edtAmount.setText(DataHelper.transactionAmount.toIndonesianNumberString())
        binding.edtDate.setText(convertDateToIndonesianFormat(DataHelper.transactionDate.substring(0, 10)))

        if (DataHelper.transactionType == "pemasukan") {
            binding.edtCategory.setText(DataHelper.transactionCategory.toIncomeCategoryText())
        } else {
            binding.edtCategory.setText(DataHelper.transactionCategory.toExpenseCategoryText())
        }
        NumberFormatHelper.setupAmountEditText(binding.edtAmount)
        DateHelper.setupDateEditText(binding.edtDate, parentFragmentManager)
    }

    private fun setupDataStoreIncome(incomeData: IncomeData) {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreference.userToken.collect {
                setupViewModel(it)
                transactionViewModel.updateIncomeTransaction(DataHelper.transactionId, incomeData)
                setupObservers()
            }
        }
    }

    private fun setupDataStoreExpense(expenseData: ExpenseData) {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreference.userToken.collect {
                setupViewModel(it)
                transactionViewModel.updateExpenseTransaction(DataHelper.transactionId, expenseData)
                setupObservers()
            }
        }
    }

    private fun setupDataStoreIncomeDelete(id: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreference.userToken.collect {
                setupViewModel(it)
                transactionViewModel.deleteIncomeTransaction(id)
                setupObservers()
            }
        }
    }

    private fun setupDataStoreExpenseDelete(id: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreference.userToken.collect {
                setupViewModel(it)
                transactionViewModel.deleteExpenseTransaction(id)
                setupObservers()
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = TransactionViewModelFactory(
            TransactionRepository(ApiClientBearer.create(token))
        )
        transactionViewModel = ViewModelProvider(
            requireActivity(),
            factory
        )[TransactionViewModel::class.java]
    }

    private fun setupObservers() {
        transactionViewModel.message.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            (activity as MainActivity).showBottomNav()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}