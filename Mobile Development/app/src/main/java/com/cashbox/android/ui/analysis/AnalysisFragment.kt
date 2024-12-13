package com.cashbox.android.ui.analysis

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.cashbox.android.R
import com.cashbox.android.data.api.ApiClientBearer
import com.cashbox.android.data.datastore.DataStoreInstance
import com.cashbox.android.data.datastore.UserPreference
import com.cashbox.android.data.model.AnalysisData
import com.cashbox.android.data.repository.TransactionRepository
import com.cashbox.android.databinding.FragmentAnalysisBinding
import com.cashbox.android.ui.viewmodel.TransactionViewModelFactory
import com.cashbox.android.utils.AnimationHelper
import com.cashbox.android.utils.NumberFormatHelper
import com.cashbox.android.utils.getColorResource
import com.cashbox.android.utils.toExpenseCategoryText
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar

class AnalysisFragment : Fragment(R.layout.fragment_analysis) {
    private val binding by viewBinding(FragmentAnalysisBinding::bind)
    private lateinit var analysisViewModel: AnalysisViewModel
    private lateinit var analysisAdapter: AnalysisAdapter
    private lateinit var calendar: Calendar
    private val userPreference by lazy {
        UserPreference(DataStoreInstance.getInstance(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupAdapter()
        setupDataStore()
    }

    private fun setupButtons() {
        calendar = Calendar.getInstance()
        AnimationHelper.applyTouchAnimation(binding.btnThisMonth)
        AnimationHelper.applyTouchAnimation(binding.btnLastMonth)
        AnimationHelper.applyTouchAnimation(binding.btnLastTwoMonth)

        binding.btnThisMonth.setOnClickListener {
            analysisViewModel.changeMenuId(THIS_MONTH)
            analysisViewModel.changeMonth(calendar.get(Calendar.MONTH) + 1)
        }
        binding.btnLastMonth.setOnClickListener {
            analysisViewModel.changeMenuId(LAST_SEVEN_DAYS)
            analysisViewModel.changeMonth(calendar.get(Calendar.MONTH))
        }
        binding.btnLastTwoMonth.setOnClickListener {
            analysisViewModel.changeMenuId(LAST_THIRTY_DAYS)
            analysisViewModel.changeMonth(calendar.get(Calendar.MONTH) - 1)
        }
    }

    private fun setupPieChart(pieEntries: List<PieEntry>, pieColors: List<Int>) {
        val pieDataSet = PieDataSet(pieEntries, resources.getString(R.string.expense_category))
        pieDataSet.setDrawValues(false)
        pieDataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        pieDataSet.colors = pieColors

        val pieData = PieData(pieDataSet)
        binding.apply {
            pieChart.data = pieData
            pieChart.description.isEnabled = false
            pieChart.legend.isEnabled = false
            pieChart.isHighlightPerTapEnabled = false
            pieChart.isDrawHoleEnabled = true
            pieChart.holeRadius = 50f
            pieChart.transparentCircleRadius = 0f
            pieChart.setEntryLabelColor(Color.BLACK)
            pieChart.setEntryLabelTextSize(10f)
            pieChart.setExtraOffsets(12f, 12f, 12f, 12f)
            pieChart.invalidate()
        }
    }

    private fun getPieEntries(data: List<AnalysisData>): List<PieEntry> {
        val pieEntries = ArrayList<PieEntry>()
        for (i in data.indices) {
            pieEntries.add(PieEntry(data[i].amount.toFloat(), data[i].category.toExpenseCategoryText()))
        }
        return pieEntries
    }

    private fun getPieSliceColors(data: List<AnalysisData>): List<Int> {
        val pieSliceColors = ArrayList<Int>()
        for (i in data.indices) {
            pieSliceColors.add(Color.parseColor(
                data[i].category.toExpenseCategoryText().getColorResource()
            ))
        }
        return pieSliceColors
    }

    private fun setupAdapter() {
        analysisAdapter = AnalysisAdapter()
        binding.rvAnalysis.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAnalysis.adapter = analysisAdapter
    }

    private fun setupDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(userPreference.userToken, userPreference.userUid) { token, uid ->
                Pair(token, uid)
            }.collect { (token, uid) ->
                setupViewModel(token)
                analysisViewModel.getInitialTransaction(uid)
                setupObservers()
            }
        }
    }

    private fun setupViewModel(token: String) {
        val factory = TransactionViewModelFactory(
            TransactionRepository(ApiClientBearer.create(token))
        )
        analysisViewModel = ViewModelProvider(
            requireActivity(),
            factory
        )[AnalysisViewModel::class.java]
    }

    private fun setupObservers() {
        analysisViewModel.isFirstTime.observe(viewLifecycleOwner) { isFirstTime ->
            if (isFirstTime) {
                binding.pieChart.animateY(1000)
                analysisViewModel.changeFirstTimeValue()
            }
        }

        analysisViewModel.thisMonthButtonStyle.observe(viewLifecycleOwner) { background ->
            setupButtonBackground(binding.btnThisMonth, background)
        }
        analysisViewModel.lastSevenDaysButtonStyle.observe(viewLifecycleOwner) { background ->
            setupButtonBackground(binding.btnLastMonth, background)
        }
        analysisViewModel.lastThirtyDaysButtonStyle.observe(viewLifecycleOwner) { background ->
            setupButtonBackground(binding.btnLastTwoMonth, background)
        }

        analysisViewModel.expenseCategories.observe(viewLifecycleOwner) { data ->
            if (data.isNotEmpty()) {
                binding.pieChart.visibility = View.VISIBLE
                binding.tvTotalExpense.visibility = View.VISIBLE
                binding.rvAnalysis.visibility = View.VISIBLE

                setupPieChart(
                    getPieEntries(data.sortedByDescending { it.amount }),
                    getPieSliceColors(data.sortedByDescending { it.amount })
                )
                analysisAdapter.submitList(data.sortedByDescending { it.amount })
                binding.tvTotalExpense.text = NumberFormatHelper.formatToRupiah(
                    data.sumOf { it.amount }
                )
            } else {
                binding.pieChart.visibility = View.INVISIBLE
                binding.tvTotalExpense.visibility = View.INVISIBLE
                binding.rvAnalysis.visibility = View.INVISIBLE
            }
        }

        analysisViewModel.month.observe(viewLifecycleOwner) { month ->
            viewLifecycleOwner.lifecycleScope.launch {
                userPreference.userUid.collect {
                    analysisViewModel.getSpecificTransaction(it, month)
                }
            }
        }

        analysisViewModel.exception.observe(viewLifecycleOwner) { exception ->
            if (exception) {
                binding.pieChart.visibility = View.INVISIBLE
                binding.tvTotalExpense.visibility = View.INVISIBLE
                binding.rvAnalysis.visibility = View.INVISIBLE
                analysisViewModel.resetExceptionValue()
            }
        }
    }

    private fun setupButtonBackground(button: TextView, background: Int) {
        button.background = ContextCompat.getDrawable(requireContext(), background)
    }

    companion object {
        const val THIS_MONTH = 1
        const val LAST_SEVEN_DAYS = 2
        const val LAST_THIRTY_DAYS = 3
    }
}