package com.cashbox.android.ui.wallet

import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import com.cashbox.android.data.repository.WalletRepository
import com.cashbox.android.databinding.FragmentWalletBinding
import com.cashbox.android.ui.main.MainActivity
import com.cashbox.android.ui.viewmodel.WalletViewModelFactory
import com.cashbox.android.utils.DataHelper
import com.cashbox.android.utils.NumberFormatHelper.formatToRupiah
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class WalletFragment : Fragment(R.layout.fragment_wallet) {
    private val binding by viewBinding(FragmentWalletBinding::bind)
    private lateinit var walletViewModel: WalletViewModel
    private lateinit var walletAdapter: WalletAdapter
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
        binding.btnAddWallet.setOnClickListener {
            findNavController().navigate(R.id.action_nav_wallet_to_nav_add_wallet)
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
        walletAdapter = WalletAdapter(object : WalletAdapter.OnItemClickListener {
            override fun onItemClick(id: Int, name: String) {
                DataHelper.walletId = id
                findNavController().navigate(R.id.action_nav_wallet_to_nav_edit_wallet)
            }
        })
        binding.rvWallet.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWallet.adapter = walletAdapter
    }

    private fun setupDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(userPreference.userToken, userPreference.userUid) { token, uid ->
                Pair(token, uid)
            }.collect { (token, uid) ->
                setupViewModel(token)
                walletViewModel.getWallet(uid)
                setupObservers()
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = WalletViewModelFactory(WalletRepository(ApiClientBearer.create(token)))
        walletViewModel = ViewModelProvider(requireActivity(), factory)[WalletViewModel::class.java]
    }

    private fun setupObservers() {
        walletViewModel.wallet.observe(viewLifecycleOwner) { listWallet ->
            walletAdapter.submitList(listWallet)
            binding.tvWalletBalance.text = formatToRupiah(listWallet.sumOf { it.amount })
        }

        walletViewModel.exception.observe(viewLifecycleOwner) { exception ->
            if (exception) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
                walletViewModel.resetExceptionValue()
            }
        }
    }
}