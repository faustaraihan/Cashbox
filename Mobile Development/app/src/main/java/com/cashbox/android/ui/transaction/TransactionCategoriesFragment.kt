package com.cashbox.android.ui.transaction

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

class TransactionCategoriesFragment : Fragment(R.layout.fragment_transaction_categories) {
    private val binding by viewBinding(FragmentTransactionCategoriesBinding::bind)
    private val addTransactionViewModel by lazy {
        ViewModelProvider(requireActivity())[AddTransactionViewModel::class.java]
    }
    private lateinit var transactionCategoriesAdapter: TransactionCategoriesAdapter
    private var transactionCategories = listOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackButton()
        setupBackPressedDispatcher()
        setupAdapter()
        setupObservers()
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
                    addTransactionViewModel.setTransactionCategory(transactionCategory)
                }
            }
        )
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = transactionCategoriesAdapter
    }

    private fun setupObservers() {
        addTransactionViewModel.transactionType.observe(viewLifecycleOwner) { transactionType ->
            if (transactionType == resources.getString(R.string.income)) {
                transactionCategories = resources.getStringArray(R.array.income_categories).toList()
                transactionCategoriesAdapter.submitList(transactionCategories)
                binding.tvCategory.text = resources.getString(R.string.income_category)
            } else {
                transactionCategories = resources.getStringArray(R.array.expense_categories).toList()
                transactionCategoriesAdapter.submitList(transactionCategories)
                binding.tvCategory.text = resources.getString(R.string.expense_category)
            }
        }
    }
}