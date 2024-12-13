package com.cashbox.android.ui.budgeting

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.cashbox.android.R
import com.cashbox.android.databinding.FragmentTransactionCategoriesBinding
import com.cashbox.android.ui.transaction.TransactionCategoriesAdapter

class BudgetingCategoriesFragment : Fragment(R.layout.fragment_transaction_categories) {
    private val binding by viewBinding(FragmentTransactionCategoriesBinding::bind)
    private val budgetingViewModel by lazy {
        ViewModelProvider(requireActivity())[BudgetingViewModel::class.java]
    }
    private lateinit var transactionCategoriesAdapter: TransactionCategoriesAdapter
    private var transactionCategories = listOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTitleText()
        setupBackButton()
        setupBackPressedDispatcher()
        setupAdapter()
    }

    private fun setupTitleText() {
        binding.tvCategory.text = resources.getString(R.string.expense_category)
    }

    private fun setupBackButton() {
        binding.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupBackPressedDispatcher() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )
    }

    private fun setupAdapter() {
        transactionCategoriesAdapter = TransactionCategoriesAdapter(
            object : TransactionCategoriesAdapter.OnItemClickListener {
                override fun onItemClick(transactionCategory: String) {
                    findNavController().popBackStack()
                    budgetingViewModel.setSelectedCategory(transactionCategory)
                }
            }
        )
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = transactionCategoriesAdapter

        transactionCategories = resources.getStringArray(R.array.expense_categories).toList()
        transactionCategoriesAdapter.submitList(transactionCategories)
    }
}