package com.cashbox.android.ui.goals

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.cashbox.android.R
import com.cashbox.android.data.api.ApiClientBearer
import com.cashbox.android.data.datastore.DataStoreInstance
import com.cashbox.android.data.datastore.UserPreference
import com.cashbox.android.data.model.GoalsBody
import com.cashbox.android.data.repository.GoalsRepository
import com.cashbox.android.databinding.FragmentEditGoalsBinding
import com.cashbox.android.ui.viewmodel.GoalsViewModelFactory
import com.cashbox.android.utils.AnimationHelper
import com.cashbox.android.utils.DataHelper
import com.cashbox.android.utils.DateHelper
import com.cashbox.android.utils.NumberFormatHelper
import com.cashbox.android.utils.toIndonesianNumberString
import com.cashbox.android.utils.toOriginalNumber
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class EditGoalsFragment : Fragment(R.layout.fragment_edit_goals) {
    private val binding by viewBinding(FragmentEditGoalsBinding::bind)
    private lateinit var addGoalsViewModel: AddGoalsViewModel
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupEditText()
    }

    private fun setupButtons() {
        binding.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }

        AnimationHelper.applyTouchAnimation(binding.btnSave)
        binding.btnSave.setOnClickListener {
            val name = binding.edtGoalsTitle.text.toString()
            val amount = binding.edtAmount.text.toString()
            val date = binding.edtDate.text.toString()

            if (name.isEmpty() || amount.isEmpty() || date.isEmpty()) {
                showToast(resources.getString(R.string.data_can_not_be_empty))
            } else {
                setupDataStore(
                    name,
                    amount.toOriginalNumber(),
                    DateHelper.convertDateToOriginalValue(date)
                )
            }
        }
    }

    private fun setupEditText() {
        binding.edtGoalsTitle.setText(DataHelper.goalsName)
        binding.edtAmount.setText(DataHelper.goalsAmount.toIndonesianNumberString())
        binding.edtDate.setText(DataHelper.goalsDate)
        NumberFormatHelper.setupAmountEditText(binding.edtAmount)
        DateHelper.setupDateEditText(binding.edtDate, parentFragmentManager)
    }

    private fun setupDataStore(name: String, amount: Long, date: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(userPreference.userToken, userPreference.userUid) { token, uid ->
                Pair(token, uid)
            }.collect { (token, uid) ->
                setupViewModel(token)
                addGoalsViewModel.editGoals(
                    DataHelper.goalsId,
                    GoalsBody(uid, name, amount, date)
                )
                setupObservers()
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = GoalsViewModelFactory(GoalsRepository(ApiClientBearer.create(token)))
        addGoalsViewModel = ViewModelProvider(requireActivity(), factory)[AddGoalsViewModel::class.java]
    }

    private fun setupObservers() {
        addGoalsViewModel.addGoalsResponse.observe(viewLifecycleOwner) { message ->
            showToast(message)
            findNavController().popBackStack()
        }

        addGoalsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.tvSave.visibility = View.GONE
                binding.pbSave.visibility = View.VISIBLE
            } else {
                binding.tvSave.visibility = View.VISIBLE
                binding.pbSave.visibility = View.GONE
            }
        }

        addGoalsViewModel.exception.observe(viewLifecycleOwner) { exception ->
            if (exception) {
                showToast(resources.getString(R.string.no_internet_connection))
                addGoalsViewModel.resetExceptionValue()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}