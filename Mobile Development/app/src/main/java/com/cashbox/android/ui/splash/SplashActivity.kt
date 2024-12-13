package com.cashbox.android.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.cashbox.android.data.datastore.DataStoreInstance
import com.cashbox.android.data.datastore.UserPreference
import com.cashbox.android.ui.auth.LoginActivity
import com.cashbox.android.ui.main.MainActivity
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { true }
        checkUserSession()
    }

    private fun checkUserSession() {
        lifecycleScope.launch {
            userPreference.isUserLogin.collect {
                if (it) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}