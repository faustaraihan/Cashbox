package com.cashbox.android.data.api

import com.cashbox.android.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    val apiClient: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}