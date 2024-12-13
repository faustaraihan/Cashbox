package com.cashbox.android.ui.budgeting

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.cashbox.android.R
import com.cashbox.android.data.api.ApiClientBearer
import com.cashbox.android.data.datastore.DataStoreInstance
import com.cashbox.android.data.datastore.UserPreference
import com.cashbox.android.data.repository.BudgetingRepository
import com.cashbox.android.databinding.FragmentBudgetingBinding
import com.cashbox.android.ui.main.MainActivity
import com.cashbox.android.ui.viewmodel.BudgetingViewModelFactory
import com.cashbox.android.utils.DataHelper
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class BudgetingFragment : Fragment(R.layout.fragment_budgeting) {
    private val binding by viewBinding(FragmentBudgetingBinding::bind)
    private lateinit var budgetingViewModel: BudgetingViewModel
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }
    private lateinit var budgetingAdapter: BudgetingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupAdapter()
        setupDataStore()
    }

    private fun setupButtons() {
        binding.btnAddBudgeting.setOnClickListener {
            findNavController().navigate(R.id.action_nav_budgeting_to_nav_add_budgeting)
            (activity as MainActivity).hideBottomNav()
        }
    }

    private fun setupAdapter() {
        budgetingAdapter = BudgetingAdapter(object : BudgetingAdapter.OnItemClickListener {
            override fun onItemClick(ids: MutableList<Int>) {
                DataHelper.budgetingIds = ids
                val dialog = BudgetingConfirmationDialog {
                    viewLifecycleOwner.lifecycleScope.launch {
                        userPreference.userUid.collect {
                            budgetingViewModel.getBudgeting(it)
                        }
                    }
                }
                dialog.show(parentFragmentManager, "DELETE_BUDGETING")
            }
        })
        binding.rvBudgeting.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBudgeting.adapter = budgetingAdapter
    }

    private fun setupDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(userPreference.userToken, userPreference.userUid) { token, uid ->
                    Pair(token, uid)
                }.collect { (token, uid) ->
                    setupViewModel(token)
                    budgetingViewModel.getBudgeting(uid)
                    setupObservers()
                }
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = BudgetingViewModelFactory(BudgetingRepository(ApiClientBearer.create(token)))
        budgetingViewModel = ViewModelProvider(
            requireActivity(),
            factory
        )[BudgetingViewModel::class.java]
    }

    private fun setupObservers() {
        budgetingViewModel.budgetingData.observe(viewLifecycleOwner) { data ->
            budgetingAdapter.submitList(data)
        }

        budgetingViewModel.exception.observe(viewLifecycleOwner) { exception ->
            if (exception) {
                showToast(resources.getString(R.string.no_internet_connection))
                budgetingViewModel.resetExceptionValue()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}