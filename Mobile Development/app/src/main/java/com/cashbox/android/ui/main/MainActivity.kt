package com.cashbox.android.ui.main

 import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.cashbox.android.R
import com.cashbox.android.databinding.ActivityMainBinding

@SuppressLint("SourceLockedOrientationActivity")
@Suppress("Deprecation")
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding by viewBinding(ActivityMainBinding::bind)
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupMenu()
        setupMenuVisibility()
    }

    private fun setupMenu() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        binding.bottomNavView.setupWithNavController(navController)

        var lastClickTime: Long = 0
        binding.btnAdd.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > 1000) {
                lastClickTime = currentTime
                navController.navigate(R.id.action_to_nav_add_transaction)
                hideBottomNav()
            }
        }
    }

    private fun setupMenuVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isMenuShowed = destination.id in getShowedMenuDestination()
            val isBackgroundStatusBar = destination.id in getBackgroundStatusBarDestination()
            listOf(binding.bottomNavView, binding.btnAdd).forEach {
                it.visibility = if (isMenuShowed) View.VISIBLE else View.GONE
            }
            changeStatusBarColor(if (isBackgroundStatusBar) R.color.background else R.color.sky_blue)
        }
    }

    private fun getShowedMenuDestination(): Set<Int> {
        return setOf(
            R.id.nav_home,
            R.id.nav_budgeting,
            R.id.nav_transaction,
            R.id.nav_analysis
        )
    }

    private fun getBackgroundStatusBarDestination(): Set<Int> {
        return setOf(
            R.id.nav_transaction_categories,
            R.id.nav_money_source,
            R.id.nav_my_account,
            R.id.nav_edit_account,
            R.id.nav_wallet,
            R.id.nav_add_wallet,
            R.id.nav_edit_wallet,
            R.id.nav_goals,
            R.id.nav_goals_detail,
            R.id.nav_add_goals,
            R.id.nav_edit_goals,
            R.id.nav_add_save,
            R.id.nav_edit_save,
            R.id.nav_add_budgeting,
            R.id.nav_budgeting_categories
        )
    }

    private fun changeStatusBarColor(color: Int) {
        val resolvedColor = ContextCompat.getColor(this, color)
        if (window.statusBarColor != resolvedColor) {
            window.statusBarColor = resolvedColor
            window.decorView.systemUiVisibility = if (color == R.color.sky_blue) 0 else
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun hideBottomNav() {
        listOf(binding.bottomNavView, binding.btnAdd).forEach {
            startAnimation(it, R.anim.slide_out_bottom)
            it.visibility = View.GONE
        }
    }

    fun showBottomNav() {
        listOf(binding.bottomNavView, binding.btnAdd).forEach {
            startAnimation(it, R.anim.slide_in_bottom)
            it.visibility = View.VISIBLE
        }
    }

    private fun startAnimation(view: View, animation: Int) {
        view.clearAnimation()
        view.startAnimation(AnimationUtils.loadAnimation(this, animation))
    }
}