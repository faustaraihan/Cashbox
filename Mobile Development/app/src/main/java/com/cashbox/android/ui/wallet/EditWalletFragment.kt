package com.cashbox.android.ui.wallet

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
import com.cashbox.android.data.model.WalletUpdateBody
import com.cashbox.android.data.repository.WalletRepository
import com.cashbox.android.databinding.FragmentEditWalletBinding
import com.cashbox.android.ui.viewmodel.WalletViewModelFactory
import com.cashbox.android.utils.AnimationHelper
import com.cashbox.android.utils.DataHelper
import kotlinx.coroutines.launch

class EditWalletFragment : Fragment(R.layout.fragment_edit_wallet) {
    private val binding by viewBinding(FragmentEditWalletBinding::bind)
    private lateinit var walletViewModel: WalletViewModel
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
        binding.deleteWallet.setOnClickListener {
            setupDataStoreDelete()
        }

        AnimationHelper.applyTouchAnimation(binding.btnSaveWallet)
        binding.btnSaveWallet.setOnClickListener {
            val name = binding.edtWalletName.text.toString()
            if (name.isEmpty()) {
                showToast(resources.getString(R.string.data_can_not_be_empty))
            } else {
                walletViewModel.updateWalletById(DataHelper.walletId, WalletUpdateBody(name))
            }
        }
    }

    private fun setupDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreference.userToken.collect {
                setupViewModel(it)
                walletViewModel.getWalletById(DataHelper.walletId)
                setupObservers()
            }
        }
    }

    private fun setupDataStoreDelete() {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreference.userToken.collect {
                setupViewModel(it)
                walletViewModel.deleteWallet(DataHelper.walletId)
                setupObservers()
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = WalletViewModelFactory(WalletRepository(ApiClientBearer.create(token)))
        walletViewModel = ViewModelProvider(requireActivity(), factory)[WalletViewModel::class.java]
    }

    private fun setupObservers() {
        walletViewModel.walletDetail.observe(viewLifecycleOwner) { detail ->
            binding.edtWalletName.setText(detail.name)
        }
        walletViewModel.messageUpdateSuccess.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                showToast(message)
                findNavController().popBackStack()
                walletViewModel.resetMessageUpdateSuccessValue()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}