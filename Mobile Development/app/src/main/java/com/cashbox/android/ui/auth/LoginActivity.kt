@file:Suppress("Deprecation")
package com.cashbox.android.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.cashbox.android.BuildConfig
import com.cashbox.android.R
import com.cashbox.android.data.api.ApiClient
import com.cashbox.android.data.datastore.DataStoreInstance
import com.cashbox.android.data.datastore.UserPreference
import com.cashbox.android.data.model.LoginBody
import com.cashbox.android.data.model.LoginGoogleBody
import com.cashbox.android.data.repository.UserRepository
import com.cashbox.android.databinding.ActivityLoginBinding
import com.cashbox.android.ui.main.MainActivity
import com.cashbox.android.ui.viewmodel.UserViewModelFactory
import com.cashbox.android.utils.AnimationHelper
import com.cashbox.android.utils.isEmailMatches
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@SuppressLint("SourceLockedOrientationActivity")
class LoginActivity : AppCompatActivity(R.layout.activity_login) {
    private val binding by viewBinding(ActivityLoginBinding::bind)
    private val loginViewModel by lazy {
        val factory = UserViewModelFactory(UserRepository(ApiClient.apiClient))
        ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(this))
    }
    private lateinit var googleSignInClient: GoogleSignInClient
    private var userPhoto = ""
    private var userEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupOrientationAndStatusBar(R.color.sky_blue)
        setupButtons()
        setupGoogleSignIn()
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
        AnimationHelper.applyTouchAnimation(binding.btnLogin)
        AnimationHelper.applyTouchAnimation(binding.btnLoginWithGoogle)

        binding.btnLogin.setOnClickListener {
            userEmail = binding.edtEmail.text.toString()
            val userPassword = binding.edtPassword.text.toString()

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                showToast(resources.getString(R.string.data_can_not_be_empty))
            } else if (!userEmail.isEmailMatches()) {
                showToast(resources.getString(R.string.invalid_email_format))
            } else {
                loginViewModel.userLogin(LoginBody(userEmail, userPassword))
            }
        }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.btnLoginWithGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun setupGoogleSignIn() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.WEB_CLIENT_ID)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(this) {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    @Deprecated("Deprecated onActivityResult Method")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                showToast(resources.getString(R.string.google_sign_in_failed))
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    userPhoto = user?.photoUrl.toString()
                    getIdToken(user!!)
                } else {
                    showToast(resources.getString(R.string.authentication_failed))
                }
            }
    }

    private fun getIdToken(user: FirebaseUser) {
        user.let {
            it.getIdToken(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result?.token
                    Log.i("TOKEN", idToken.toString())
                    loginViewModel.userLoginWithGoogle(LoginGoogleBody(idToken!!))
                } else {
                    showToast(resources.getString(R.string.get_token_failed))
                }
            }
        }
    }

    private fun setupObservers() {
        loginViewModel.loginResponse.observe(this) { response ->
            lifecycleScope.launch {
                userPreference.updateUserLoginStatusAndToken(true, response.token)
                userPreference.updateUserData(
                    userPhoto,
                    response.user.name,
                    userEmail,
                    response.user.uid
                )
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        }

        loginViewModel.loginGoogleResponse.observe(this) { response ->
            lifecycleScope.launch {
                userPreference.updateUserLoginStatusAndToken(true, response.token)
                userPreference.updateUserData(
                    userPhoto,
                    response.user.name,
                    response.user.email,
                    response.user.uid
                )
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        }

        loginViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.tvLogin.visibility = View.GONE
                binding.pbLogin.visibility = View.VISIBLE
            } else {
                binding.tvLogin.visibility = View.VISIBLE
                binding.pbLogin.visibility = View.GONE
            }
        }

        loginViewModel.errorMessage.observe(this) { message ->
            showToast(message)
        }

        loginViewModel.exception.observe(this) { exception ->
            if (exception) {
                showToast(resources.getString(R.string.no_internet_connection))
                loginViewModel.resetExceptionValue()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val RC_SIGN_IN = 1001
    }
}