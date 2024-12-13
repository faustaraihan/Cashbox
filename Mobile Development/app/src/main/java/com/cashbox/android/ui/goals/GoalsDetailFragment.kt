package com.cashbox.android.ui.goals

import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import com.cashbox.android.databinding.FragmentGoalsDetailBinding
import com.cashbox.android.ui.viewmodel.GoalsViewModelFactory
import com.cashbox.android.utils.AnimationHelper
import com.cashbox.android.utils.DataHelper
import com.cashbox.android.utils.DateHelper.convertDateToIndonesianFormat
import com.cashbox.android.utils.DateHelper.convertDateToOriginalValue
import com.cashbox.android.utils.NumberFormatHelper.formatToRupiah
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class GoalsDetailFragment : Fragment(R.layout.fragment_goals_detail) {
    private val binding by viewBinding(FragmentGoalsDetailBinding::bind)
    private lateinit var goalsViewModel: GoalsViewModel
    private lateinit var saveAdapter: SaveAdapter
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupButtons()
        setupDataStore()
    }

    private fun setupButtons() {
        binding.ibBack.setOnClickListener {
            findNavController().popBackStack()
            goalsViewModel.resetExceptionValue()
        }
        binding.ibDelete.setOnClickListener {
            goalsViewModel.deleteGoals(DataHelper.goalsId)
        }
        binding.ibEdit.setOnClickListener {
            DataHelper.goalsDate = binding.tvGoalsDay.text.toString()
            findNavController().navigate(R.id.action_nav_goals_detail_to_nav_edit_goals)
        }

        AnimationHelper.applyTouchAnimation(binding.btnAddSave)
        binding.btnAddSave.setOnClickListener {
            findNavController().navigate(R.id.action_nav_goals_detail_to_nav_add_save)
            goalsViewModel.resetExceptionValue()
        }
    }

    private fun setupAdapter() {
        saveAdapter = SaveAdapter(object : SaveAdapter.OnItemClickListener {
            override fun onItemClick(id: Int, description: String, amount: Long, date: String) {
                DataHelper.saveId = id
                DataHelper.saveDescription = description
                DataHelper.saveAmount = amount
                DataHelper.saveDate = date
                findNavController().navigate(R.id.action_nav_goals_detail_to_nav_edit_save)
            }
        })
        binding.rvSaveHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSaveHistory.adapter = saveAdapter
    }

    private fun setupDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(userPreference.userToken, userPreference.userUid) { token, uid ->
                Pair(token, uid)
            }.collect { (token, uid) ->
                setupViewModel(token)
                goalsViewModel.getGoalsDetail(DataHelper.goalsId)
                goalsViewModel.getSavesByGoal(DataHelper.goalsId, uid)
                setupObservers()
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = GoalsViewModelFactory(GoalsRepository(ApiClientBearer.create(token)))
        goalsViewModel = ViewModelProvider(requireActivity(), factory)[GoalsViewModel::class.java]
    }

    private fun setupObservers() {
        goalsViewModel.goal.observe(viewLifecycleOwner) { goal ->
            binding.tvTitle.text = goal.name
            binding.tvGoalsAmountRest.text = resources.getString(
                R.string.to_target_amount,
                formatToRupiah(goal.amount)
            )
            binding.tvGoalsDay.text = convertDateToIndonesianFormat(
                goal.targetDate.substring(0, 10)
            )
            binding.tvRemainingDays.text = resources.getString(
                R.string.target_difference_day,
                ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    LocalDate.parse(goal.targetDate.substring(0, 10))
                )
            )
        }

        goalsViewModel.savesByGoal.observe(viewLifecycleOwner) { savesByGoal ->
            saveAdapter.submitList(savesByGoal)
        }

        goalsViewModel.differences.observe(viewLifecycleOwner) { (target, current) ->
            binding.tvGoalsAmountProgress.text = formatToRupiah(current)
            binding.tvGoalsPercentage.text = ((current.toDouble()/target) * 100).toInt().toString()
            binding.pbGoals.progress = ((current.toDouble()/target) * 100).toInt()
            binding.tvAmount.text = formatToRupiah(target - current)
        }

        goalsViewModel.deleteMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                goalsViewModel.resetDeleteMessageValue()
            }
        }

        goalsViewModel.exception.observe(viewLifecycleOwner) { exception ->
            if (exception) {
                binding.tvGoalsAmountProgress.text = formatToRupiah(0L)
                binding.tvGoalsPercentage.text = "0"
                binding.pbGoals.progress = 0
                binding.tvRemainingDays.text = resources.getString(
                    R.string.target_difference_day,
                    ChronoUnit.DAYS.between(
                        LocalDate.now(),
                        LocalDate.parse(convertDateToOriginalValue(binding.tvGoalsDay.text.toString()))
                    )
                )
                binding.tvAmount.text = formatToRupiah(DataHelper.goalsAmount)
                saveAdapter.submitList(listOf())
            }
        }
    }
}