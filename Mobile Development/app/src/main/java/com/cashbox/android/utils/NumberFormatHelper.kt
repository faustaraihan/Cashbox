package com.cashbox.android.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.util.Locale

object NumberFormatHelper {
    fun formatToRupiah(amount: Long): String {
        val localeID = Locale("in", "ID")
        val formatter = NumberFormat.getCurrencyInstance(localeID)
        formatter.maximumFractionDigits = 0
        return formatter.format(amount)
    }

    fun setupAmountEditText(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val inputAmount = s.toString()
                if (inputAmount.isNotEmpty()) {
                    val formattedAmount = inputAmount.toOriginalNumber().toIndonesianNumberString()
                    updateAmountEditText(editText, formattedAmount, this)
                }
            }
        })
    }

    private fun updateAmountEditText(editText: EditText, newText: String, watcher: TextWatcher) {
        editText.removeTextChangedListener(watcher)
        editText.setText(newText)
        editText.setSelection(newText.length)
        editText.addTextChangedListener(watcher)
    }
}