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
import com.cashbox.android.data.model.SaveBody
import com.cashbox.android.data.repository.GoalsRepository
import com.cashbox.android.databinding.FragmentAddSaveBinding
import com.cashbox.android.ui.viewmodel.GoalsViewModelFactory
import com.cashbox.android.utils.AnimationHelper
import com.cashbox.android.utils.DataHelper
import com.cashbox.android.utils.DateHelper
import com.cashbox.android.utils.DateHelper.convertDateToOriginalValue
import com.cashbox.android.utils.NumberFormatHelper
import com.cashbox.android.utils.toOriginalNumber
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AddSaveFragment : Fragment(R.layout.fragment_add_save) {
    private val binding by viewBinding(FragmentAddSaveBinding::bind)
    private lateinit var addSaveViewModel: AddSaveViewModel
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
            val description = binding.edtDescription.text.toString()
            val amount = binding.edtAmount.text.toString()
            val date = binding.edtDate.text.toString()

            if (description.isEmpty() || amount.isEmpty() || date.isEmpty()) {
                showToast(resources.getString(R.string.data_can_not_be_empty))
            } else {
                setupDataStore(
                    description,
                    amount.toOriginalNumber(),
                    convertDateToOriginalValue(date)
                )
            }
        }
    }

    private fun setupEditText() {
        NumberFormatHelper.setupAmountEditText(binding.edtAmount)
        DateHelper.setupDateEditText(binding.edtDate, parentFragmentManager)
    }

    private fun setupDataStore(description: String, amount: Long, date: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(userPreference.userToken, userPreference.userUid) { token, uid ->
                Pair(token, uid)
            }.collect { (token, uid) ->
                setupViewModel(token)
                addSaveViewModel.addSave(SaveBody(uid, DataHelper.goalsId, description, amount, date))
                setupObservers()
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = GoalsViewModelFactory(GoalsRepository(ApiClientBearer.create(token)))
        addSaveViewModel = ViewModelProvider(requireActivity(), factory)[AddSaveViewModel::class.java]
    }

    private fun setupObservers() {
        addSaveViewModel.addSaveResponse.observe(viewLifecycleOwner) { message ->
            showToast(message)
            findNavController().popBackStack()
        }

        addSaveViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.tvSave.visibility = View.GONE
                binding.pbSave.visibility = View.VISIBLE
            } else {
                binding.tvSave.visibility = View.VISIBLE
                binding.pbSave.visibility = View.GONE
            }
        }

        addSaveViewModel.exception.observe(viewLifecycleOwner) { exception ->
            if (exception) {
                showToast(resources.getString(R.string.no_internet_connection))
                addSaveViewModel.resetExceptionValue()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}