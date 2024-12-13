package com.cashbox.android.ui.account

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
import com.cashbox.android.data.model.AccountBody
import com.cashbox.android.data.repository.AccountRepository
import com.cashbox.android.databinding.FragmentEditAccountBinding
import com.cashbox.android.ui.viewmodel.AccountViewModelFactory
import com.cashbox.android.utils.AnimationHelper
import com.cashbox.android.utils.DateHelper
import com.cashbox.android.utils.DateHelper.convertDateToOriginalValue
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class EditAccountFragment : Fragment(R.layout.fragment_edit_account) {
    private val binding by viewBinding(FragmentEditAccountBinding::bind)
    private lateinit var accountViewModel: AccountViewModel
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }
    private var userGender = "L"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupBirthDateEditText()
        setupGenderRadioButton()
        setupDataStore()
    }

    private fun setupButtons() {
        binding.apply {
            ibBack.setOnClickListener {
                findNavController().popBackStack()
            }

            AnimationHelper.applyTouchAnimation(btnSave)
            btnSave.setOnClickListener {
                val birthDate = binding.edtUserBirthDate.text.toString()
                val number = binding.edtUserNumber.text.toString()

                if (birthDate.isEmpty() || number.isEmpty()) {
                    showToast(resources.getString(R.string.data_can_not_be_empty))
                } else {
                    updateUserData(convertDateToOriginalValue(birthDate), number)
                }
            }
        }
    }

    private fun setupBirthDateEditText() {
        DateHelper.setupDateEditText(binding.edtUserBirthDate, parentFragmentManager)
    }

    private fun setupGenderRadioButton() {
        binding.btnRadioMale.setOnClickListener {
            binding.btnRadioMale.setImageResource(R.drawable.ic_btn_radio_checked)
            binding.btnRadioFemale.setImageResource(R.drawable.ic_btn_radio_unchecked)
            userGender = "L"
        }

        binding.btnRadioFemale.setOnClickListener {
            binding.btnRadioMale.setImageResource(R.drawable.ic_btn_radio_unchecked)
            binding.btnRadioFemale.setImageResource(R.drawable.ic_btn_radio_checked)
            userGender = "P"
        }
    }

    private fun setupDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(userPreference.userToken, userPreference.userUid) { token, uid ->
                Pair(token, uid)
            }.collect { (token, uid) ->
                setupViewModel(token)
                accountViewModel.getUserData(uid)
                setupObservers()
            }
        }
    }

    private fun updateUserData(birthDate: String, number: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreference.userUid.collect {
                accountViewModel.updateUserData(AccountBody(it, birthDate, number, userGender))
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = AccountViewModelFactory(AccountRepository(ApiClientBearer.create(token)))
        accountViewModel = ViewModelProvider(requireActivity(), factory)[AccountViewModel::class.java]
    }

    private fun setupObservers() {
        accountViewModel.userData.observe(viewLifecycleOwner) { userData ->
            binding.edtUserName.setText(userData.name)
            binding.edtUserEmail.setText(userData.email)
            binding.edtUserNumber.setText(userData.number)
            binding.edtUserBirthDate.setText(
                DateHelper.convertDateToIndonesianFormat(userData.birthDate.substring(0, 10))
            )
            userGender = if (userData.gender == "L") {
                binding.btnRadioMale.setImageResource(R.drawable.ic_btn_radio_checked)
                binding.btnRadioFemale.setImageResource(R.drawable.ic_btn_radio_unchecked)
                "L"
            } else {
                binding.btnRadioMale.setImageResource(R.drawable.ic_btn_radio_unchecked)
                binding.btnRadioFemale.setImageResource(R.drawable.ic_btn_radio_checked)
                "P"
            }
        }

        accountViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.tvSave.visibility = View.GONE
                binding.pbSave.visibility = View.VISIBLE
            } else {
                binding.tvSave.visibility = View.VISIBLE
                binding.pbSave.visibility = View.GONE
            }
        }

        accountViewModel.message.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                showToast(message)
                findNavController().popBackStack()
                accountViewModel.resetMessageValue()
            }
        }

        accountViewModel.exception.observe(viewLifecycleOwner) { exception ->
            if (exception) {
                showToast(resources.getString(R.string.no_internet_connection))
                accountViewModel.resetExceptionValue()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}