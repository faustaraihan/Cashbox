package com.cashbox.android.ui.transaction

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.cashbox.android.R
import com.cashbox.android.data.api.ApiClientBearer
import com.cashbox.android.data.datastore.DataStoreInstance
import com.cashbox.android.data.datastore.UserPreference
import com.cashbox.android.data.model.ExpenseBody
import com.cashbox.android.data.model.IncomeBody
import com.cashbox.android.data.repository.TransactionRepository
import com.cashbox.android.databinding.FragmentAddTransactionBinding
import com.cashbox.android.ui.main.MainActivity
import com.cashbox.android.ui.viewmodel.TransactionViewModelFactory
import com.cashbox.android.utils.AnimationHelper
import com.cashbox.android.utils.DataHelper
import com.cashbox.android.utils.DateHelper
import com.cashbox.android.utils.NumberFormatHelper
import com.cashbox.android.utils.getNumberId
import com.cashbox.android.utils.toOriginalNumber
import kotlinx.coroutines.launch

class AddTransactionFragment : Fragment(R.layout.fragment_add_transaction) {
    private val binding by viewBinding(FragmentAddTransactionBinding::bind)
    private lateinit var addTransactionViewModel: AddTransactionViewModel
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupBackPressedDispatcher()
        setupEditText()
        setupDataStore()
    }

    private fun setupButtons() {
        binding.apply {
            ibClose.setOnClickListener {
                findNavController().popBackStack()
                (activity as MainActivity).showBottomNav()
            }

            AnimationHelper.applyTouchAnimation(btnIncome)
            AnimationHelper.applyTouchAnimation(btnExpense)
            AnimationHelper.applyTouchAnimation(btnAdd)

            btnIncome.setOnClickListener {
                addTransactionViewModel.changeTransactionType(resources.getString(R.string.income))
                DataHelper.transactionType = resources.getString(R.string.income)
            }
            btnExpense.setOnClickListener {
                addTransactionViewModel.changeTransactionType(resources.getString(R.string.expense))
                DataHelper.transactionType = resources.getString(R.string.expense)
            }
            btnAdd.setOnClickListener {
                val description = binding.edtDescription.text.toString()
                val amount = binding.edtAmount.text.toString()
                val category = binding.edtCategory.text.toString()
                val source = binding.edtWallet.text.toString()
                val date = DateHelper.convertDateToOriginalValue(binding.edtDate.text.toString())

                if (listOf(description, amount, category, source, date).any { it.isEmpty() }) {
                    showToast(resources.getString(R.string.data_can_not_be_empty))
                } else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        userPreference.userUid.collect {
                            if (DataHelper.transactionType == resources.getString(R.string.income)) {
                                addTransactionViewModel.addIncomeTransaction(
                                    IncomeBody(
                                        it,
                                        description,
                                        amount.toOriginalNumber(),
                                        DataHelper.walletId,
                                        date,
                                        category.getNumberId(),
                                        DataHelper.walletName
                                    )
                                )
                            } else {
                                addTransactionViewModel.addExpenseTransaction(
                                    ExpenseBody(
                                        it,
                                        description,
                                        amount.toOriginalNumber(),
                                        DataHelper.walletId,
                                        date,
                                        category.getNumberId(),
                                        DataHelper.walletName
                                    )
                                )
                            }
                        }
                    }
                }
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
        NumberFormatHelper.setupAmountEditText(binding.edtAmount)
        DateHelper.setupDateEditText(binding.edtDate, parentFragmentManager)

        binding.edtCategory.setOnClickListener {
            findNavController().navigate(
                R.id.action_nav_add_transaction_to_nav_transaction_categories
            )
        }
        binding.edtWallet.setOnClickListener {
            findNavController().navigate(
                R.id.action_nav_add_transaction_to_nav_money_source
            )
        }
    }

    private fun setupDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreference.userToken.collect {
                setupViewModel(it)
                setupObservers()
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = TransactionViewModelFactory(
            TransactionRepository(ApiClientBearer.create(token))
        )
        addTransactionViewModel = ViewModelProvider(
            requireActivity(),
            factory
        )[AddTransactionViewModel::class.java]
    }

    private fun setupObservers() {
        addTransactionViewModel.transactionCategory.observe(viewLifecycleOwner) { category ->
            binding.edtCategory.setText(category)
        }
        addTransactionViewModel.transactionSource.observe(viewLifecycleOwner) { source ->
            binding.edtWallet.setText(source)
        }
        addTransactionViewModel.incomeButtonBackground.observe(viewLifecycleOwner) { background ->
            binding.btnIncome.background = ContextCompat.getDrawable(requireContext(), background)
        }
        addTransactionViewModel.expenseButtonBackground.observe(viewLifecycleOwner) { background ->
            binding.btnExpense.background = ContextCompat.getDrawable(requireContext(), background)
        }
        addTransactionViewModel.responseMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                showToast(message)
                findNavController().popBackStack()
                (activity as MainActivity).showBottomNav()
                addTransactionViewModel.resetResponseMessageValue()
            }
        }
        addTransactionViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.tvAdd.visibility = View.GONE
                binding.pbAdd.visibility = View.VISIBLE
            } else {
                binding.tvAdd.visibility = View.VISIBLE
                binding.pbAdd.visibility = View.GONE
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}