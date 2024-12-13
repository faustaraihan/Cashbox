package com.cashbox.android.ui.budgeting

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.cashbox.android.R
import com.cashbox.android.data.api.ApiClientBearer
import com.cashbox.android.data.datastore.DataStoreInstance
import com.cashbox.android.data.datastore.UserPreference
import com.cashbox.android.data.repository.BudgetingRepository
import com.cashbox.android.databinding.DialogConfirmationBinding
import com.cashbox.android.ui.viewmodel.BudgetingViewModelFactory
import com.cashbox.android.utils.DataHelper
import kotlinx.coroutines.launch

class BudgetingConfirmationDialog(private val onDismissCallback: () -> Unit) : DialogFragment(R.layout.dialog_confirmation) {
    private val binding by viewBinding(DialogConfirmationBinding::bind)
    private lateinit var budgetingViewModel: BudgetingViewModel
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(false)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissCallback()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTitle()
        setupButton()
    }

    private fun setupTitle() {
        binding.dialogTitle.text = resources.getString(R.string.delete_budgeting_confirmation)
    }

    private fun setupButton() {
        binding.btnCancel.setOnClickListener {
            dialog?.dismiss()
        }
        binding.btnSubmit.setOnClickListener {
            setupDataStore()
        }
    }

    private fun setupDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreference.userToken.collect {
                setupViewModel(it)
                for (i in DataHelper.budgetingIds.indices) {
                    budgetingViewModel.deleteBudgeting(DataHelper.budgetingIds[i])
                }
                Toast.makeText(
                    requireContext(),
                    "Budgeting berhasil dihapus",
                    Toast.LENGTH_SHORT
                ).show()
                dialog?.dismiss()
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = BudgetingViewModelFactory(BudgetingRepository(ApiClientBearer.create(token)))
        budgetingViewModel = ViewModelProvider(
            requireActivity(),
            factory
        )[BudgetingViewModel::class.java]
    }
}