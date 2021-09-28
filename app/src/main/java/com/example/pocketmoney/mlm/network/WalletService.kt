package com.example.pocketmoney.mlm.network

import com.example.pocketmoney.mlm.model.TransactionDetailModel
import com.example.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface WalletService {

    @POST("Wallet/ViewTransactionsDetails")
    suspend fun viewTransactionDetails(
        @Query("id") transactionId: String
    ): TransactionDetailModel
}