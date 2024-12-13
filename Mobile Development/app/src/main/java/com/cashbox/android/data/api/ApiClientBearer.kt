package com.cashbox.android.data.api

import com.cashbox.android.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClientBearer {
    companion object {
        fun create(token: String): ApiService {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(request)
                }
                .build()

            val apiClient = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL_API)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return apiClient.create(ApiService::class.java)
        }
    }
}