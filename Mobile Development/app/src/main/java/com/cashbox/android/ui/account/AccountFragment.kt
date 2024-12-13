package com.cashbox.android.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.cashbox.android.R
import com.cashbox.android.data.datastore.DataStoreInstance
import com.cashbox.android.data.datastore.UserPreference
import com.cashbox.android.databinding.FragmentAccountBinding
import com.cashbox.android.ui.auth.LoginActivity
import com.cashbox.android.ui.main.MainActivity
import com.cashbox.android.utils.AnimationHelper
import kotlinx.coroutines.launch

class AccountFragment : Fragment(R.layout.fragment_account) {
    private val binding by viewBinding(FragmentAccountBinding::bind)
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupBackPressedDispatcher()
        setupUserData()
    }

    private fun setupButtons() {
        binding.apply {
            ibClose.setOnClickListener {
                findNavController().popBackStack()
                (activity as MainActivity).showBottomNav()
            }

            AnimationHelper.applyTouchAnimation(btnMyAccount)
            AnimationHelper.applyTouchAnimation(btnLogout)

            btnMyAccount.setOnClickListener {
                findNavController().navigate(R.id.action_nav_account_to_nav_my_account)
            }
            btnLogout.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    userPreference.updateUserLoginStatusAndToken(false, "")
                    userPreference.updateUserData("", "", "", "")
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
            }
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

    private fun setupUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
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
            launch {
                userPreference.username.collect {
                    binding.tvUsername.text = it
                }
            }
            launch {
                userPreference.userEmail.collect {
                    binding.tvEmail.text = it
                }
            }
        }
    }
}