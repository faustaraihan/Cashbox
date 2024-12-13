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
import com.cashbox.android.data.model.WalletAddBody
import com.cashbox.android.data.repository.WalletRepository
import com.cashbox.android.databinding.FragmentAddWalletBinding
import com.cashbox.android.ui.viewmodel.WalletViewModelFactory
import com.cashbox.android.utils.AnimationHelper
import com.cashbox.android.utils.NumberFormatHelper
import com.cashbox.android.utils.toOriginalNumber
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AddWalletFragment : Fragment(R.layout.fragment_add_wallet) {
    private val binding by viewBinding(FragmentAddWalletBinding::bind)
    private lateinit var walletViewModel: WalletViewModel
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupAmountEditText()
    }

    private fun setupButtons() {
        binding.apply {
            ibBack.setOnClickListener {
                findNavController().popBackStack()
            }

            AnimationHelper.applyTouchAnimation(btnAddWallet)
            btnAddWallet.setOnClickListener {
                val name = binding.edtWalletName.text.toString()
                val amount = binding.edtAmount.text.toString()

                if (name.isEmpty() || amount.isEmpty()) {
                    showToast(resources.getString(R.string.data_can_not_be_empty))
                } else {
                    setupDataStore(name, amount.toOriginalNumber())
                }
            }
        }
    }

    private fun setupAmountEditText() {
        NumberFormatHelper.setupAmountEditText(binding.edtAmount)
    }

    private fun setupDataStore(name: String, amount: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(userPreference.userToken, userPreference.userUid) { token, uid ->
                Pair(token, uid)
            }.collect { (token, uid) ->
                setupViewModel(token)
                walletViewModel.addWallet(WalletAddBody(uid, name, amount))
                setupObservers()
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = WalletViewModelFactory(WalletRepository(ApiClientBearer.create(token)))
        walletViewModel = ViewModelProvider(requireActivity(), factory)[WalletViewModel::class.java]
    }

    private fun setupObservers() {
        walletViewModel.messageAddSuccess.observe(viewLifecycleOwner) { message ->
            showToast(message)
            findNavController().popBackStack()
        }

        walletViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.tvAddWallet.visibility = View.GONE
                binding.pbAddWallet.visibility = View.VISIBLE
            } else {
                binding.tvAddWallet.visibility = View.VISIBLE
                binding.pbAddWallet.visibility = View.GONE
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}