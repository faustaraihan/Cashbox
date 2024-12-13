package com.cashbox.android.ui.account

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.cashbox.android.R
import com.cashbox.android.data.api.ApiClientBearer
import com.cashbox.android.data.datastore.DataStoreInstance
import com.cashbox.android.data.datastore.UserPreference
import com.cashbox.android.data.repository.AccountRepository
import com.cashbox.android.databinding.FragmentMyAccountBinding
import com.cashbox.android.ui.viewmodel.AccountViewModelFactory
import com.cashbox.android.utils.DateHelper.convertDateToIndonesianFormat
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MyAccountFragment : Fragment(R.layout.fragment_my_account) {
    private val binding by viewBinding(FragmentMyAccountBinding::bind)
    private lateinit var accountViewModel: AccountViewModel
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupDataStore()
    }

    private fun setupButtons() {
        binding.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.editAccount.setOnClickListener {
            findNavController().navigate(R.id.action_nav_my_account_to_nav_edit_account)
        }
    }

    private fun setupDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                combine(userPreference.userToken, userPreference.userUid) { token, uid ->
                    Pair(token, uid)
                }.collect { (token, uid) ->
                    setupViewModel(token)
                    accountViewModel.getUserData(uid)
                    setupObservers()
                }
            }
            launch {
                userPreference.userPhoto.collect {
                    Glide.with(requireContext())
                        .load(it.toUri())
                        .centerCrop()
                        .transform(CircleCrop())
                        .placeholder(R.drawable.ic_account)
                        .error(R.drawable.ic_account)
                        .into(binding.ivProfile)
                }
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = AccountViewModelFactory(AccountRepository(ApiClientBearer.create(token)))
        accountViewModel = ViewModelProvider(requireActivity(), factory)[AccountViewModel::class.java]
    }

    private fun setupObservers() {
        accountViewModel.userData.observe(viewLifecycleOwner) { userData ->
            binding.tvName.text = userData.name
            binding.tvEmail.text = userData.email
            binding.tvNumber.text = userData.number
            binding.tvBirthDate.text =
                convertDateToIndonesianFormat(userData.birthDate.substring(0, 10))
            binding.tvGender.text = if (userData.gender == "L") "Laki-laki" else "Perempuan"
        }
    }
}