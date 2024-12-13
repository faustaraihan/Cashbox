package com.cashbox.android.ui.goals

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.cashbox.android.R
import com.cashbox.android.data.api.ApiClientBearer
import com.cashbox.android.data.datastore.DataStoreInstance
import com.cashbox.android.data.datastore.UserPreference
import com.cashbox.android.data.repository.GoalsRepository
import com.cashbox.android.databinding.FragmentGoalsBinding
import com.cashbox.android.ui.main.MainActivity
import com.cashbox.android.ui.viewmodel.GoalsViewModelFactory
import com.cashbox.android.utils.DataHelper
import com.cashbox.android.utils.NumberFormatHelper.formatToRupiah
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class GoalsFragment : Fragment(R.layout.fragment_goals) {
    private val binding by viewBinding(FragmentGoalsBinding::bind)
    private lateinit var goalsViewModel: GoalsViewModel
    private lateinit var goalsAdapter: GoalsAdapter
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupBackPressedDispatcher()
        setupAdapter()
        setupDataStore()
    }

    private fun setupButtons() {
        binding.ibBack.setOnClickListener {
            findNavController().popBackStack()
            (activity as MainActivity).showBottomNav()
        }
        binding.tvAddGoals.setOnClickListener {
            findNavController().navigate(R.id.action_nav_goals_to_nav_add_goals)
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

    private fun setupAdapter() {
        goalsAdapter = GoalsAdapter(object : GoalsAdapter.OnItemClickListener {
            override fun onItemClick(id: Int, name: String, amount: Long) {
                DataHelper.goalsId = id
                DataHelper.goalsName = name
                DataHelper.goalsAmount = amount
                findNavController().navigate(R.id.action_nav_goals_to_nav_goals_detail)
            }
        })
        binding.rvGoals.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGoals.adapter = goalsAdapter
    }

    private fun setupDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(userPreference.userToken, userPreference.userUid) { token, uid ->
                Pair(token, uid)
            }.collect { (token, uid) ->
                setupViewModel(token)
                goalsViewModel.getListGoals(uid)
                setupObservers()
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = GoalsViewModelFactory(GoalsRepository(ApiClientBearer.create(token)))
        goalsViewModel = ViewModelProvider(requireActivity(), factory)[GoalsViewModel::class.java]
    }

    private fun setupObservers() {
        goalsViewModel.listGoals.observe(viewLifecycleOwner) { listGoals ->
            goalsAdapter.submitList(listGoals)
        }
        goalsViewModel.saves.observe(viewLifecycleOwner) { saves ->
            binding.tvGoalsBalance.text = formatToRupiah(saves.sumOf { it.amount })
        }
    }
}