package com.cashbox.android.ui.auth

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.cashbox.android.R
import com.cashbox.android.data.api.ApiClient
import com.cashbox.android.data.model.RegisterBody
import com.cashbox.android.data.repository.UserRepository
import com.cashbox.android.databinding.ActivityRegisterBinding
import com.cashbox.android.ui.viewmodel.UserViewModelFactory
import com.cashbox.android.utils.AnimationHelper
import com.cashbox.android.utils.isEmailMatches

@SuppressLint("SourceLockedOrientationActivity")
@Suppress("Deprecation")
class RegisterActivity : AppCompatActivity(R.layout.activity_register) {
    private val binding by viewBinding(ActivityRegisterBinding::bind)
    private val registerViewModel by lazy {
        val factory = UserViewModelFactory(UserRepository(ApiClient.apiClient))
        ViewModelProvider(this, factory)[RegisterViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupOrientationAndStatusBar(R.color.background)
        setupButtons()
        setupObservers()
    }

    private fun setupOrientationAndStatusBar(color: Int) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val resolvedColor = ContextCompat.getColor(this, color)
        if (window.statusBarColor != resolvedColor) {
            window.statusBarColor = resolvedColor
            window.decorView.systemUiVisibility = if (color == R.color.sky_blue) 0 else
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun setupButtons() {
        AnimationHelper.applyTouchAnimation(binding.btnRegister)

        binding.ibBack.setOnClickListener {
            finish()
        }
        binding.btnRegister.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            val confirmPassword = binding.edtConfirmPassword.text.toString()

            if (listOf(name, email, password, confirmPassword).any { it.isEmpty() }) {
                showToast(resources.getString(R.string.data_can_not_be_empty))
            } else if (password != confirmPassword) {
                showToast(resources.getString(R.string.incorrect_confirmation_password))
            } else if (!email.isEmailMatches()) {
                showToast(resources.getString(R.string.invalid_email_format))
            } else {
                registerViewModel.userRegister(RegisterBody(name, email, password))
            }
        }
    }

    private fun setupObservers() {
        registerViewModel.registerResponse.observe(this) { response ->
            showToast(response.message)
            finish()
        }

        registerViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.tvRegister.visibility = View.GONE
                binding.pbRegister.visibility = View.VISIBLE
            } else {
                binding.tvRegister.visibility = View.VISIBLE
                binding.pbRegister.visibility = View.GONE
            }
        }

        registerViewModel.errorMessage.observe(this) { message ->
            showToast(message)
        }

        registerViewModel.exception.observe(this) { exception ->
            if (exception) {
                showToast(resources.getString(R.string.no_internet_connection))
                registerViewModel.resetExceptionValue()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}