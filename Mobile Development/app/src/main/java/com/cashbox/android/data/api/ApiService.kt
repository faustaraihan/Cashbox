package com.cashbox.android.data.api

import com.cashbox.android.data.model.AccountBody
import com.cashbox.android.data.model.AccountHeader
import com.cashbox.android.data.model.AnalysisHeader
import com.cashbox.android.data.model.BudgetingBody
import com.cashbox.android.data.model.BudgetingHeader
import com.cashbox.android.data.model.ExpenseBody
import com.cashbox.android.data.model.ExpenseData
import com.cashbox.android.data.model.GoalsBody
import com.cashbox.android.data.model.GoalsListHeader
import com.cashbox.android.data.model.GoalsSingleHeader
import com.cashbox.android.data.model.IncomeBody
import com.cashbox.android.data.model.IncomeData
import com.cashbox.android.data.model.LoginBody
import com.cashbox.android.data.model.LoginGoogleBody
import com.cashbox.android.data.model.LoginResponse
import com.cashbox.android.data.model.RegisterBody
import com.cashbox.android.data.model.RegisterResponse
import com.cashbox.android.data.model.SaveBody
import com.cashbox.android.data.model.SaveHeader
import com.cashbox.android.data.model.TransactionHeader
import com.cashbox.android.data.model.TransactionResponse
import com.cashbox.android.data.model.WalletAddBody
import com.cashbox.android.data.model.WalletGetByIdHeader
import com.cashbox.android.data.model.WalletGetHeader
import com.cashbox.android.data.model.WalletPostResponse
import com.cashbox.android.data.model.WalletUpdateBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/auth/google-login")
    suspend fun userLoginWithGoogle(@Body loginGoogleBody: LoginGoogleBody): Response<LoginResponse>

    @POST("api/auth/login")
    suspend fun userLogin(@Body loginBody: LoginBody): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun userRegister(@Body registerBody: RegisterBody): Response<RegisterResponse>

    @POST("api/wallet")
    suspend fun addWallet(@Body walletAddBody: WalletAddBody): WalletPostResponse

    @GET("api/wallet")
    suspend fun getWallet(): WalletGetHeader

    @GET("api/wallet/{id}")
    suspend fun getWalletById(@Path("id") id: Int): WalletGetByIdHeader

    @PUT("api/wallet/{id}")
    suspend fun updateWalletById(
        @Path("id") id: Int,
        @Body walletUpdateBody: WalletUpdateBody
    ): WalletPostResponse

    @DELETE("api/wallet/{id}")
    suspend fun deleteWallet(@Path("id") id: Int): TransactionResponse

    @GET("api/transaksi/transaksi")
    suspend fun getAllTransaction(): TransactionHeader

    @POST("api/pemasukan")
    suspend fun addIncomeTransaction(@Body incomeBody: IncomeBody): TransactionResponse

    @POST("api/pengeluaran")
    suspend fun addExpenseTransaction(@Body expenseBody: ExpenseBody): TransactionResponse

    @PUT("api/pemasukan/{id_pemasukan}")
    suspend fun updateIncomeTransactionById(
        @Path("id_pemasukan") id: Int,
        @Body incomeData: IncomeData
    ): TransactionResponse

    @DELETE("api/pemasukan/{id_pemasukan}")
    suspend fun deleteIncomeTransaction(@Path("id_pemasukan") id: Int): TransactionResponse

    @PUT("api/pengeluaran/{id_pengeluaran}")
    suspend fun updateExpenseTransactionById(
        @Path("id_pengeluaran") id: Int,
        @Body expenseData: ExpenseData
    ): TransactionResponse

    @DELETE("api/pengeluaran/{id_pengeluaran}")
    suspend fun deleteExpenseTransaction(@Path("id_pengeluaran") id: Int): TransactionResponse

    @GET("api/transaksi/bulan")
    suspend fun getTransactionOnSpecificMonth(
        @Query("bulan") month: Int,
        @Query("tahun") year: Int
    ): AnalysisHeader

    @GET("api/goals")
    suspend fun getAllGoals(): GoalsListHeader

    @POST("api/goals")
    suspend fun addGoals(@Body goalsBody: GoalsBody): TransactionResponse

    @PUT("api/goals/{id}")
    suspend fun editGoals(
        @Path("id") id: Int,
        @Body goalsBody: GoalsBody
    ): TransactionResponse

    @DELETE("api/goals/{id}")
    suspend fun deleteGoals(@Path("id") id: Int): TransactionResponse

    @GET("api/tabungan")
    suspend fun getAllSaves(): SaveHeader

    @GET("api/goals/{id}")
    suspend fun getGoalsDetail(@Path("id") id: Int): GoalsSingleHeader

    @GET("api/tabungan/goal/{id}")
    suspend fun getSaveByGoals(@Path("id") id: Int, @Query("uid") uid: String): SaveHeader

    @POST("api/tabungan")
    suspend fun addSave(@Body saveBody: SaveBody): TransactionResponse

    @PUT("api/tabungan/{id}")
    suspend fun editSave(
        @Path("id") id: Int,
        @Body saveBody: SaveBody
    ): TransactionResponse

    @DELETE("api/tabungan/{id}")
    suspend fun deleteSave(@Path("id") id: Int, @Query("uid") uid: String): TransactionResponse

    @GET("api/user/{uid}")
    suspend fun getUserData(@Path("uid") uid: String): AccountHeader

    @PUT("api/user")
    suspend fun updateUserData(@Body accountBody: AccountBody): TransactionResponse

    @POST("api/budgeting")
    suspend fun addBudgeting(@Body budgetingBody: BudgetingBody)

    @GET("api/budgeting")
    suspend fun getBudgeting(): BudgetingHeader

    @DELETE("api/budgeting/{id}")
    suspend fun deleteBudgeting(@Path("id") id: Int): TransactionResponse
}