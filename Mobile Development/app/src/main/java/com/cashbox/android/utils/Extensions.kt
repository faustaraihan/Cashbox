package com.cashbox.android.utils

import com.cashbox.android.R
import java.text.NumberFormat
import java.util.Locale

fun Long.toIndonesianNumberString(): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale("in", "ID"))
    return numberFormat.format(this)
}

fun String.toOriginalNumber(): Long {
    return this.replace(".", "").toLong()
}

fun String.getImageResource(): Int {
    return when (this) {
        "Gaji" -> R.drawable.ic_income_salary
        "Bonus" -> R.drawable.ic_income_bonuses
        "Usaha Sampingan" -> R.drawable.ic_income_side_business
        "Investasi (Pemasukan)" -> R.drawable.ic_income_investment
        "Pemasukan Lain" -> R.drawable.ic_income_more
        "Makanan & Minuman" -> R.drawable.ic_expense_food
        "Transportasi" -> R.drawable.ic_expense_transportation
        "Kesehatan" -> R.drawable.ic_expense_health
        "Tagihan & Utilitas" -> R.drawable.ic_expense_utility
        "Pendidikan" -> R.drawable.ic_expense_education
        "Hiburan" -> R.drawable.ic_expense_entertainment
        "Belanja" -> R.drawable.ic_expense_shopping
        "Investasi (Pengeluaran)" -> R.drawable.ic_expense_investment
        "Perawatan Pribadi" -> R.drawable.ic_expense_personal_care
        "Donasi" -> R.drawable.ic_expense_donation
        "Asuransi" -> R.drawable.ic_expense_insurance
        "Kebutuhan Rumah Tangga" -> R.drawable.ic_expense_household
        "Pajak" -> R.drawable.ic_expense_tax
        "Pengeluaran Lain" -> R.drawable.ic_expense_more
        else -> 0
    }
}

fun String.getColorResource(): String {
    return when (this) {
        "Makanan & Minuman" -> "#FF9800"
        "Transportasi" -> "#2979FF"
        "Kesehatan" -> "#FF5252"
        "Tagihan & Utilitas" -> "#424242"
        "Pendidikan" -> "#40C4FF"
        "Hiburan" -> "#D500F9"
        "Belanja" -> "#FF4081"
        "Investasi (Pengeluaran)" -> "#00BFA5"
        "Perawatan Pribadi" -> "#AB47BC"
        "Donasi" -> "#26C6DA"
        "Asuransi" -> "#263238"
        "Kebutuhan Rumah Tangga" -> "#8D6E63"
        "Pajak" -> "#FFC107"
        "Pengeluaran Lain" -> "#B0BEC5"
        else -> ""
    }
}

fun String.getNumberId(): Int {
    return when (this) {
        "Gaji", "Makanan & Minuman" -> 1
        "Bonus", "Transportasi" -> 2
        "Usaha Sampingan", "Kesehatan" -> 3
        "Investasi (Pemasukan)", "Tagihan & Utilitas" -> 4
        "Pemasukan Lain", "Pendidikan" -> 5
        "Hiburan" -> 6
        "Belanja" -> 7
        "Investasi (Pengeluaran)" -> 8
        "Perawatan Pribadi" -> 9
        "Donasi" -> 10
        "Asuransi" -> 11
        "Kebutuhan Rumah Tangga" -> 12
        "Pajak" -> 13
        "Pengeluaran Lain" -> 14
        else -> 0
    }
}

fun Int.toIncomeCategoryText(): String {
    return when (this) {
        1 -> "Gaji"
        2 -> "Bonus"
        3 -> "Usaha Sampingan"
        4 -> "Investasi (Pemasukan)"
        5 -> "Pemasukan lain"
        else -> ""
    }
}

fun Int.toExpenseCategoryText(): String {
    return when (this) {
        1 -> "Makanan & Minuman"
        2 -> "Transportasi"
        3 -> "Kesehatan"
        4 -> "Tagihan & Utilitas"
        5 -> "Pendidikan"
        6 -> "Hiburan"
        7 -> "Belanja"
        8 -> "Investasi (Pengeluaran)"
        9 -> "Perawatan Pribadi"
        10 -> "Donasi"
        11 -> "Asuransi"
        12 -> "Kebutuhan Rumah Tangga"
        13 -> "Pajak"
        14 -> "Pengeluaran Lain"
        else -> ""
    }
}

fun String.toEncodingNumber(): Int {
    return when (this) {
        "Makanan & Minuman" -> 2
        "Transportasi" -> 5
        "Kesehatan" -> 1
        "Tagihan & Utilitas" -> 3
        "Pendidikan" -> 4
        "Hiburan" -> 12
        "Belanja" -> 11
        "Investasi (Pengeluaran)" -> 9
        "Perawatan Pribadi" -> 10
        "Donasi" -> 8
        "Asuransi" -> 6
        "Kebutuhan Rumah Tangga" -> 7
        "Pajak" -> 13
        "Pengeluaran Lain" -> 14
        else -> 0
    }
}

fun String.isEmailMatches(): Boolean {
    val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
    return this.matches(emailRegex)
}