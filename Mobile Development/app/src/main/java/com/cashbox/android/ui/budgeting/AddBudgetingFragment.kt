package com.cashbox.android.ui.budgeting

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.cashbox.android.R
import com.cashbox.android.data.api.ApiClientBearer
import com.cashbox.android.data.datastore.DataStoreInstance
import com.cashbox.android.data.datastore.UserPreference
import com.cashbox.android.data.model.BudgetingBody
import com.cashbox.android.data.repository.BudgetingRepository
import com.cashbox.android.databinding.FragmentAddBudgetingBinding
import com.cashbox.android.ml.PredictModel
import com.cashbox.android.ui.main.MainActivity
import com.cashbox.android.ui.viewmodel.BudgetingViewModelFactory
import com.cashbox.android.utils.AnimationHelper
import com.cashbox.android.utils.NumberFormatHelper
import com.cashbox.android.utils.getNumberId
import com.cashbox.android.utils.toEncodingNumber
import com.cashbox.android.utils.toOriginalNumber
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AddBudgetingFragment : Fragment(R.layout.fragment_add_budgeting) {
    private val binding by viewBinding(FragmentAddBudgetingBinding::bind)
    private lateinit var budgetingViewModel: BudgetingViewModel
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupBackPressedDispatcher()
        setupDataStore()
    }

    private fun setupButtons() {
        NumberFormatHelper.setupAmountEditText(binding.edtAmount)
        binding.ibBack.setOnClickListener {
            findNavController().popBackStack()
            (activity as MainActivity).showBottomNav()
        }

        binding.edtCategory.setOnClickListener {
            findNavController().navigate(R.id.action_nav_add_budgeting_to_nav_budgeting_categories)
        }

        AnimationHelper.applyTouchAnimation(binding.btnSave)
        binding.btnSave.setOnClickListener {
            val category = binding.edtCategory.text.toString()
            val amount = binding.edtAmount.text.toString()

            if (category.isEmpty() || amount.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.data_can_not_be_empty),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                calculateBudgeting(category, amount.toOriginalNumber())
            }
            budgetingViewModel.resetSelectedCategory()
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

    private fun setupDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreference.userToken.collect {
                setupViewModel(it)
                setupObservers()
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

    private fun setupObservers() {
        budgetingViewModel.responseSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(
                    requireContext(),
                    "Rekomendasi budgeting berhasil ditambahkan",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
                budgetingViewModel.resetResponseValue()
            }
        }

        budgetingViewModel.selectedCategory.observe(viewLifecycleOwner) { category ->
            binding.edtCategory.setText(category)
        }
    }

    private fun calculateBudgeting(category: String, amount: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreference.userUid.collect {
                runModel(it, category, amount)
            }
        }
    }

    private fun runModel(uid: String, category: String, amount: Long) {
        val model = PredictModel.newInstance(requireContext())

        val inputValues = floatArrayOf(
            (amount.toDouble()/1000000).toFloat(),
            (category.toEncodingNumber().toFloat())
        )

        val byteBuffer = ByteBuffer.allocateDirect(4 * inputValues.size)
            .order(ByteOrder.nativeOrder())
        inputValues.forEach { byteBuffer.putFloat(it) }

        val inputFeature0 = TensorBuffer.createFixedSize(
            intArrayOf(1, 2),
            DataType.FLOAT32
        )
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
        val outputArray = outputFeature0.floatArray

        budgetingViewModel.addBudgeting(
            BudgetingBody(
                uid,
                category.getNumberId(),
                amount,
                outputArray[0]
            )
        )
        model.close()
    }
}