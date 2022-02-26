package com.jmm.network.services

import com.jmm.model.TransactionDetailModel
import com.jmm.model.mlmModels.CustomerAuthBalanceResponse
import com.jmm.model.serviceModels.PaytmResponseModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WalletService {

    @POST("Wallet/ViewTransactionsDetails")
    suspend fun viewTransactionDetails(
        @Query("id") transactionId: String
    ): TransactionDetailModel

    @GET("Wallet/CheckCustomerBalanceAndAuth")
    suspend fun getCustomerAuthWithBalance(
        @Query("userid") userId: String,
        @Query("roleid") roleId: Int
    ): CustomerAuthBalanceResponse

    @POST("Wallet/OnlineAddMoneyToWallet")
    suspend fun addMoneyToWallet(
        @Body data: PaytmResponseModel,
    ): Boolean
}