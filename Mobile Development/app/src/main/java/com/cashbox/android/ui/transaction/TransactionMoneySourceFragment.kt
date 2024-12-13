package com.cashbox.android.ui.transaction

import android.os.Bundle
import android.view.View
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
import com.cashbox.android.data.repository.TransactionRepository
import com.cashbox.android.databinding.FragmentMoneySourceBinding
import com.cashbox.android.ui.viewmodel.TransactionViewModelFactory
import com.cashbox.android.ui.wallet.WalletAdapter
import com.cashbox.android.utils.DataHelper
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TransactionMoneySourceFragment : Fragment(R.layout.fragment_money_source) {
    private val binding by viewBinding(FragmentMoneySourceBinding::bind)
    private lateinit var addTransactionViewModel: AddTransactionViewModel
    private lateinit var walletAdapter: WalletAdapter
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackButton()
        setupAdapter()
        setupDataStore()
    }

    private fun setupBackButton() {
        binding.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupAdapter() {
        walletAdapter = WalletAdapter(object : WalletAdapter.OnItemClickListener {
            override fun onItemClick(id: Int, name: String) {
                addTransactionViewModel.setTransactionSource(name)
                DataHelper.walletId = id
                DataHelper.walletName = name
                findNavController().popBackStack()
            }
        })
        binding.rvMoneySource.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMoneySource.adapter = walletAdapter
    }

    private fun setupDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(userPreference.userToken, userPreference.userUid) { token, uid ->
                Pair(token, uid)
            }.collect { (token, uid) ->
                setupViewModel(token)
                addTransactionViewModel.getAllWallet(uid)
                setupObservers()
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = TransactionViewModelFactory(
            TransactionRepository(ApiClientBearer.create(token))
        )
        addTransactionViewModel = ViewModelProvider(
            requireActivity(),
            factory
        )[AddTransactionViewModel::class.java]
    }

    private fun setupObservers() {
        addTransactionViewModel.wallet.observe(viewLifecycleOwner) {
            walletAdapter.submitList(it)
        }
    }
}